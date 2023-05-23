package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.*;
import com.gg.server.domain.game.dto.req.NormalResultReqDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.team.exception.TeamIdNotMatchException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final TeamUserRepository teamUserRepository;
    private final RankRedisService rankRedisService;
    private final PChangeService pChangeService;

    @Transactional(readOnly = true)
    public GameTeamInfo getUserGameInfo(Long gameId, Long userId) {
        List<GameTeamUserInfo> infos = gameRepository.findTeamGameUser(gameId);
        return new GameTeamInfo(infos, userId);
    }

    @Transactional
    public synchronized Boolean createRankResult(RankResultReqDto scoreDto, Long userId) {
        log.info("create Rank Result");
        // 현재 게임 id
        Game game = findByGameId(scoreDto.getGameId());
        if (game.getStatus() != StatusType.WAIT) {
            return false;
        }
        return updateScore(game, scoreDto, game.getSeason().getId(), userId);
    }

    @Transactional
    public synchronized Boolean normalExpResult(NormalResultReqDto normalResultReqDto) {
        Game game = findByGameId(normalResultReqDto.getGameId());
        List<TeamUser> teamUsers = teamUserRepository.findAllByGameId(game.getId());
        if (teamUsers.size() == 2 && game.getStatus() == StatusType.WAIT) {
            expUpdates(game, teamUsers);
            pChangeService.addPChange(game, teamUsers.get(0).getUser(), null);
            pChangeService.addPChange(game, teamUsers.get(1).getUser(), null);
            return true;
        } else if (teamUsers.size() != 2) {
            throw new InvalidParameterException("team 이 잘못되었습니다.", ErrorCode.VALID_FAILED);
        }
        return false;
    }

    private void expUpdates(Game game, List<TeamUser> teamUsers) {
        LocalDateTime time = getToday(game.getStartTime());
        for (TeamUser tu :
                teamUsers) {
            expUpdate(tu, time);
        }
        game.updateStatus();
    }

    private void expUpdate(TeamUser teamUser, LocalDateTime time) {
        Integer gamePerDay = teamUserRepository.findByDateAndUser(time, teamUser.getUser().getId());
        teamUser.getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
    }

    private static LocalDateTime getToday(LocalDateTime gameTime) {
        return LocalDateTime.of(gameTime.getYear(), gameTime.getMonthValue(), gameTime.getDayOfMonth(), 0, 0);
    }

    private void setTeamScore(TeamUser tu, int teamScore, Boolean isWin) {
        tu.getTeam().inputScore(teamScore);
        tu.getTeam().setWin(isWin);
    }

    private TeamUser findTeamId(Long teamId, List<TeamUser> teamUsers) {
        for (TeamUser tu :
                teamUsers) {
            if (tu.getTeam().getId().equals(teamId)) {
                return tu;
            }
        }
        throw new TeamIdNotMatchException();
    }
    private Boolean updateScore(Game game, RankResultReqDto scoreDto, Long seasonId, Long userId) {
        log.info("update score");
        List<TeamUser> teams = teamUserRepository.findAllByGameId(game.getId());
        TeamUser myTeam = findTeamId(scoreDto.getMyTeamId(), teams);
        TeamUser enemyTeam = findTeamId(scoreDto.getEnemyTeamId(), teams);
        if (!myTeam.getUser().getId().equals(userId)) {
            throw new InvalidParameterException("team user 정보 불일치.", ErrorCode.VALID_FAILED);
        } else {
            if (myTeam.getTeam().getScore().equals(scoreDto.getMyTeamScore())
                    && enemyTeam.getTeam().getScore().equals(scoreDto.getEnemyTeamScore())) {
                expUpdates(game, teams);
                rankRedisService.updateRankRedis(teams, seasonId, game);
            } else {
                setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
            }
            return true;
        }
    }

    public Game findByGameId(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(GameNotExistException::new);
    }
}
