package com.gg.server.domain.game;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.req.NormalResultReqDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.exception.custom.NotExistException;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final TeamUserRepository teamUserRepository;
    private final RankRedisService rankRedisService;
    private final RankRepository rankRepository;
    @Transactional(readOnly = true)
    public GameListResDto normalGameList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto rankGameList(int pageNum, int pageSize, Long seasonId) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto allGameList(int pageNum, int pageSize, StatusType status) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games;
        if (status != null) {
            games = gameRepository.findAllByAndStatusIn(Arrays.asList(StatusType.END, StatusType.LIVE), pageable);
        } else {
            games = gameRepository.findAllByAndStatus(StatusType.END, pageable);
        }
        return new GameListResDto(getGameResultList(games), games.isLast());
    }
    private List<GameResultResDto> getGameResultList(Slice<Game> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games.stream().map(Game::getId).collect(Collectors.toList()));
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }

    @Transactional
    public synchronized Boolean createRankResult(RankResultReqDto scoreDto, Long userId) {
        log.info("create Rank Result");
        // rank 점수 입력받기
        if (scoreDto.getMyTeamScore() + scoreDto.getEnemyTeamScore() > 3 || scoreDto.getMyTeamScore() == scoreDto.getEnemyTeamScore()) {
            throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
        }
        // 현재 게임 id
        Game game = gameRepository.findById(scoreDto.getGameId())
                .orElseThrow(() -> new NotExistException("존재하지 않는 게임 Id 입니다.", ErrorCode.BAD_ARGU));
        if (game.getStatus() != StatusType.WAIT) {
            return false;
        }
        log.info("update score");
        return updateScore(game, scoreDto, game.getSeason().getId(), userId);
    }

    public synchronized Boolean normalExpResult(NormalResultReqDto normalResultReqDto) {
        Game game = gameRepository.findById(normalResultReqDto.getGameId())
                .orElseThrow(() -> new NotExistException("존재하지 않는 게임 id 입니다.", ErrorCode.NOT_FOUND));
        List<TeamUser> teamUsers = teamUserRepository.findAllByGameId(game.getId());
        if (teamUsers.size() == 2 && game.getStatus() == StatusType.LIVE) {
            LocalDateTime now = LocalDateTime.now();
            int gamePerDay = teamUserRepository.findByDateAndUser(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0),
                    teamUsers.get(0).getUser().getId());
            teamUsers.get(0).getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
            gamePerDay = teamUserRepository.findByDateAndUser(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0),
                    teamUsers.get(1).getUser().getId());
            teamUsers.get(1).getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
            game.updateStatus();
            return true;
        } else if (teamUsers.size() != 2) {
            throw new InvalidParameterException("team 이 잘못되었습니다.", ErrorCode.VALID_FAILED);
        }
        return false;
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
        return null;
    }
    private Boolean updateScore(Game game, RankResultReqDto scoreDto, Long seasonId, Long userId) {
        List<TeamUser> teams = teamUserRepository.findAllByGameId(game.getId());
        TeamUser myTeam = findTeamId(scoreDto.getMyTeamId(), teams);
        TeamUser enemyTeam = findTeamId(scoreDto.getEnemyTeamId(), teams);
        if (myTeam == null || enemyTeam == null || !myTeam.getUser().getId().equals(userId)) {
            throw new NotExistException("잘못된 team Id 입니다.", ErrorCode.NOT_FOUND);
        } else {
            if (myTeam.getTeam().getScore() == scoreDto.getMyTeamScore()
                    && enemyTeam.getTeam().getScore() == scoreDto.getEnemyTeamScore()) {
                game.updateStatus();
                rankRedisService.updateRankRedis(teams, seasonId, game);

            } else {
                setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
            }
            return true;
        }
    }
}
