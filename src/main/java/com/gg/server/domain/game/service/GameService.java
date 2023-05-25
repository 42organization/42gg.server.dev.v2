package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.*;
import com.gg.server.domain.game.dto.req.NormalResultReqDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.game.exception.ScoreNotMatchedException;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
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
        if (infos.size() == 0) {
            throw new GameNotExistException();
        }
        return new GameTeamInfo(infos, userId);
    }

    @Transactional
    public synchronized Boolean createRankResult(RankResultReqDto scoreDto, Long userId) {
        log.info("create Rank Result");
        // 현재 게임 id
        Game game = findByGameId(scoreDto.getGameId());
        if (game.getStatus() != StatusType.WAIT && game.getStatus() != StatusType.LIVE) {
            return false;
        }
        return updateScore(game, scoreDto, game.getSeason().getId(), userId);
    }

    @Transactional
    public synchronized Boolean normalExpResult(NormalResultReqDto normalResultReqDto) {
        Game game = findByGameId(normalResultReqDto.getGameId());
        List<TeamUser> teamUsers = teamUserRepository.findAllByGameId(game.getId());
        if (teamUsers.size() == 2 &&
                (game.getStatus() == StatusType.WAIT || game.getStatus() == StatusType.LIVE)) {
            expUpdates(game, teamUsers);
            log.info("3");
            pChangeService.addPChange(game, teamUsers.get(0).getUser(), null);
            log.info("5");
            pChangeService.addPChange(game, teamUsers.get(1).getUser(), null);
            log.info("6");
            return true;
        } else if (teamUsers.size() != 2) {
            throw new InvalidParameterException("team 이 잘못되었습니다.", ErrorCode.VALID_FAILED);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public ExpChangeResultResDto expChangeResult(Long gameId, Long userId) {
        List<PChange> pChanges = pChangeService.findExpChangeHistory(gameId, userId);
        if (pChanges.size() == 1) {
            return new ExpChangeResultResDto(0, pChanges.get(0).getExp());
        } else {
            log.info("before:", pChanges.get(1).getExp(), ", after: ", pChanges.get(0).getExp());
            return new ExpChangeResultResDto(pChanges.get(1).getExp(), pChanges.get(0).getExp());
        }
    }

    @Transactional(readOnly = true)
    public PPPChangeResultResDto pppChangeResult(Long gameId, Long userId) throws PChangeNotExistException {
        Season season = findByGameId(gameId).getSeason();
        List<PChange> pChanges = pChangeService.findPPPChangeHistory(gameId, userId, season.getId());
        if (pChanges.size() == 1) {
            return new PPPChangeResultResDto(0, pChanges.get(0).getExp(), season.getStartPpp(), pChanges.get(0).getPppResult());
        } else {
            log.info("before:", pChanges.get(1).getExp(), ", after: ", pChanges.get(0).getExp());
            return new PPPChangeResultResDto(pChanges.get(1).getExp(), pChanges.get(0).getExp(), pChanges.get(1).getPppResult(), pChanges.get(0).getPppResult());
        }
    }

    public void expUpdates(Game game, List<TeamUser> teamUsers) {
        LocalDateTime time = getToday(game.getStartTime());
        for (TeamUser tu :
                teamUsers) {
            expUpdate(tu, time);
        }
        log.info("1");
        if (game.getStatus() == StatusType.LIVE) {
            game.updateStatus();
        }
        game.updateStatus();
        log.info("2");
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
            } else if (myTeam.getTeam().getScore().equals(0) && enemyTeam.getTeam().getScore().equals(0)){
                setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
            } else {
                // team score 초기화
                setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
                // score 가 일치하지 않다는 에러
                throw new ScoreNotMatchedException();
            }
            return true;
        }
    }

    public Game findByGameId(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(GameNotExistException::new);
    }
}
