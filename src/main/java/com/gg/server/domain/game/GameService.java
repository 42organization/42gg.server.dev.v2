package com.gg.server.domain.game;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.req.NormalResultReqDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.season.dto.CurSeason;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.global.utils.EloRating;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
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
    private final RankRedisRepository rankRedisRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
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
    public synchronized Boolean createRankResult(RankResultReqDto scoreDto) {
        // rank 점수 입력받기
        if (scoreDto.getMyTeamScore() + scoreDto.getEnemyTeamScore() > 3) {
            throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
        }
        // 현재 게임 id
        Game game = gameRepository.findById(scoreDto.getGameId())
                .orElseThrow(() -> new NotExistException("존재하지 않는 게임 Id 입니다.", ErrorCode.BAD_ARGU));
        if (game.getStatus() != StatusType.WAIT) {
            return false;
        }
        // user 가 게임한 팀, 상대 팀 id
        if (!updateScore(game, scoreDto, new CurSeason(game.getSeason()))) {
            return false;
        }
        return true;
    }

    public Boolean normalExpResult(NormalResultReqDto normalResultReqDto) {
        Game game = gameRepository.findById(normalResultReqDto.getGameId())
                .orElseThrow(() -> new NotExistException("존재하지 않는 게임 id 입니다.", ErrorCode.NOT_FOUND));
        List<TeamUser> teamUsers = teamUserRepository.findAllByGameId(game.getId());
        if (teamUsers.size() == 2) {
            LocalDateTime now = LocalDateTime.now();
            int gamePerDay = teamUserRepository.findByDateAndUser(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0),
                    teamUsers.get(0).getUser().getId());
            teamUsers.get(0).getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
            gamePerDay = teamUserRepository.findByDateAndUser(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0),
                    teamUsers.get(1).getUser().getId());
            teamUsers.get(1).getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
        }
        game.updateStatus();
        return true;
    }
    private void setTeamScore(TeamUser tu, int teamScore, Boolean isWin) {
        tu.getTeam().inputScore(teamScore);
        tu.getTeam().setWin(isWin);
    }

    private Boolean updateScore(Game game, RankResultReqDto scoreDto, CurSeason season) {
        List<TeamUser> teams = teamUserRepository.findAllByGameId(game.getId());
        Boolean check1 = false, check2 = false;
        for (TeamUser team : teams) {
            if (team.getTeam().getId().equals(scoreDto.getMyTeamId())) {
                // my team id
                if (team.getTeam().getScore() == -1 || team.getTeam().getScore() != scoreDto.getMyTeamScore()) {
                    // 점수 입력한적 없으면 || 점수 입력 있는데 다른 점수이면 값 update
                    setTeamScore(team, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                    check1 = false;
                }
                else // 점수 입력 있는데 같은 점수
                    check1 = true;
            } else if (team.getTeam().getId().equals(scoreDto.getEnemyTeamId())) {
                if (team.getTeam().getScore() == -1 || team.getTeam().getScore() != scoreDto.getEnemyTeamScore()) {
                    setTeamScore(team, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
                    check2 = false;
                }
                else
                    check2 = true;
            } else {
                check1 = false;
                check2 = false;
            }
        }
        if (check1 && check2) {
            game.updateStatus();
            updateRankRedis(teams, season);
        }
        return true;
    }

    void updateRankRedis(List<TeamUser> list, CurSeason season) {
        // 단식 -> 2명 기준
        String key = RedisKeyManager.getHashKey(season.getId());
        String zsetKey = RedisKeyManager.getZSetKey(season.getId());
        RankRedis myTeam = rankRedisRepository.findRankByUserId(key, list.get(0).getUser().getId());
        RankRedis enemyTeam = rankRedisRepository.findRankByUserId(key, list.get(1).getUser().getId());
        updatePPP(list.get(0), myTeam, enemyTeam, list.get(1).getTeam().getScore());
        updatePPP(list.get(1), enemyTeam, myTeam, list.get(0).getTeam().getScore());
        rankRedisRepository.updateRankData(key, list.get(0).getUser().getId(), myTeam);
        rankRedisRepository.deleteFromZSet(zsetKey, list.get(0).getUser().getId());
        rankRedisRepository.addToZSet(zsetKey, list.get(0).getUser().getId(), myTeam.getPpp());
        rankRedisRepository.updateRankData(key, list.get(1).getUser().getId(), enemyTeam);
        rankRedisRepository.deleteFromZSet(zsetKey, list.get(1).getUser().getId());
        rankRedisRepository.addToZSet(zsetKey, list.get(1).getUser().getId(), enemyTeam.getPpp());
    }

    void updatePPP(TeamUser teamuser, RankRedis myTeam, RankRedis enemyTeam, int enemyScore) {
        int win = teamuser.getTeam().getWin() ? myTeam.getWins() + 1 : myTeam.getWins();
        int losses = !teamuser.getTeam().getWin() ? myTeam.getLosses() + 1: myTeam.getLosses();
        myTeam.updateRank(EloRating.pppChange(myTeam.getPpp(), enemyTeam.getPpp(),
                        teamuser.getTeam().getWin(), Math.abs(teamuser.getTeam().getScore() - enemyScore) == 2),
                win, losses);
    }
}
