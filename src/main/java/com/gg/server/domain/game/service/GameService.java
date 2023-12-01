package com.gg.server.domain.game.service;

import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.coin.service.UserCoinChangeService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.*;
import com.gg.server.domain.game.dto.req.NormalResultReqDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.service.RedisUploadService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.team.exception.TeamIdNotMatchException;
import com.gg.server.domain.tier.service.TierService;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final TeamUserRepository teamUserRepository;
    private final RankRedisService rankRedisService;
    private final PChangeService pChangeService;
    private final PChangeRepository pChangeRepository;
    private final GameFindService gameFindService;
    private final UserCoinChangeService userCoinChangeService;
    private final TierService tierService;
    private final RedisUploadService redisUploadService;

  /**
   * 게임 정보를 가져온다.
   *
   * @param gameId
   * @param userId
   * @return GameTeamInfo 게임 정보
   * @throws GameNotExistException 게임이 존재하지 않음
   */
    @Transactional(readOnly = true)
    public GameTeamInfo getUserGameInfo(Long gameId, Long userId) {
        List<GameTeamUserInfo> infos = gameRepository.findTeamGameUser(gameId);
        if (infos.isEmpty()) {
            throw new GameNotExistException();
        }
        return new GameTeamInfo(infos, userId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "rankGameListByIntra", allEntries = true),
            @CacheEvict(value = "rankGameList", allEntries = true),
            @CacheEvict(value = "allGameList", allEntries = true),
            @CacheEvict(value = "allGameListByUser", allEntries = true)
    })

    /**
     * rank 게임 결과를 입력한다.
     * WAIT, LIVE 상태일 때만 입력 가능
     * @return Boolean 입력 성공 여부
     */
    public Boolean createRankResult(RankResultReqDto scoreDto, Long userId) {
        // 현재 게임 id
        Game game = gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId());
        if (game.getStatus() != StatusType.WAIT && game.getStatus() != StatusType.LIVE) {
            return false;
        }
        return updateScore(game, scoreDto, userId);
    }


    /**
     * normal 게임을 종료하고 exp(경험치)를 부여한다.
     * @return Boolean 입력 성공 여부: false
     * @throws InvalidParameterException team 정보가 잘못되었을 때
     * @throws PChangeNotExistException pchange 정보가 없을 때
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "normalGameListByIntra", allEntries = true),
            @CacheEvict(value = "normalGameList", allEntries = true),
            @CacheEvict(value = "allGameList", allEntries = true),
            @CacheEvict(value = "allGameListByUser", allEntries = true)
    })
    public Boolean normalExpResult(NormalResultReqDto normalResultReqDto, Long loginUserId) {
        Game game = gameFindService.findGameWithPessimisticLockById(normalResultReqDto.getGameId());
        List<TeamUser> teamUsers = teamUserRepository.findAllByGameId(game.getId());
        if (teamUsers.size() == 2 &&
                (game.getStatus() == StatusType.WAIT || game.getStatus() == StatusType.LIVE)) {
            expUpdates(game, teamUsers);
            savePChange(game, teamUsers, loginUserId);
            return true;
        } else if (teamUsers.size() == 2 && game.getStatus() == StatusType.END) {
            updatePchangeIsChecked(game, loginUserId);
            return true;
        } else if (teamUsers.size() != 2) {
            throw new InvalidParameterException("team 이 잘못되었습니다.", ErrorCode.VALID_FAILED);
        }
        // BEFORE 상태일 때 false
        return false;
    }

    /**
     * normal 게임에 대한 exp 변화 결과를 가져온다.
     * @param gameId
     * @param userId
     * @return ExpChangeResultResDto 경험치 변화 결과
     */
    @Transactional
    public ExpChangeResultResDto expChangeResult(Long gameId, Long userId) {
        List<PChange> pChanges = pChangeService.findExpChangeHistory(gameId, userId);
        UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addNormalGameCoin(userId);

        if (pChanges.size() == 1) {
            return new ExpChangeResultResDto(0, pChanges.get(0).getExp(), userGameCoinResultDto);
        } else {
            return new ExpChangeResultResDto(pChanges.get(1).getExp(), pChanges.get(0).getExp(), userGameCoinResultDto);
        }
    }

    /**
     * rank 게임에 대한 exp, ppp 변화 결과를 가져온다.
     * @param gameId 게임 id
     * @param userId 게임에 참여한 유저 id
     * @return PPPChangeResultResDto 경험치 변화 결과
     * @throws PChangeNotExistException
     */
    @Transactional
    public PPPChangeResultResDto pppChangeResult(Long gameId, Long userId) throws PChangeNotExistException {
        Season season = gameFindService.findByGameId(gameId).getSeason();
        List<PChange> pppHistory = pChangeService.findPPPChangeHistory(gameId, userId, season.getId());
        List<PChange> expHistory = pChangeService.findExpChangeHistory(gameId, userId);
        UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addRankGameCoin(gameId, userId);
        return new PPPChangeResultResDto(expHistory.size() <= 1 ? 0 : expHistory.get(1).getExp(),
                pppHistory.get(0).getExp(),
                pppHistory.size() <= 1 ? season.getStartPpp() : pppHistory.get(1).getPppResult(),
                pppHistory.get(0).getPppResult(), userGameCoinResultDto);
    }

    public void expUpdates(Game game, List<TeamUser> teamUsers) {
        LocalDateTime time = getToday(game.getStartTime());
        for (TeamUser tu :
                teamUsers) {
            expUpdate(tu, time);
        }
        if (game.getStatus() == StatusType.LIVE) {
            game.updateStatus();
        }
        game.updateStatus();
    }

    /**
     * PRIVATE METHOD
     */
    private void updatePchangeIsChecked(Game game, Long loginUserId) {
        pChangeRepository.findPChangeByUserIdAndGameId(loginUserId, game.getId())
                .ifPresentOrElse(pChange -> {
                    pChange.checkPChange();
                    pChangeRepository.save(pChange);
                }, () -> {
                    throw new PChangeNotExistException();
                });
    }

    private void savePChange(Game game, List<TeamUser> teamUsers, Long loginUserId) {
        Long team1UserId = teamUsers.get(0).getUser().getId();
        Long team2UserId = teamUsers.get(1).getUser().getId();
        pChangeService.addPChange(game, teamUsers.get(0).getUser(),
                rankRedisService.getUserPpp(team1UserId, game.getSeason().getId()), team1UserId.equals(loginUserId));
        pChangeService.addPChange(game, teamUsers.get(1).getUser(),
                rankRedisService.getUserPpp(team2UserId, game.getSeason().getId()), team2UserId.equals(loginUserId));
    }

    private void expUpdate(TeamUser teamUser, LocalDateTime time) {
        Integer gamePerDay = teamUserRepository.findByDateAndUser(time, teamUser.getUser().getId());
        teamUser.getUser().addExp(ExpLevelCalculator.getExpPerGame() + (ExpLevelCalculator.getExpBonus() * gamePerDay));
    }

    private static LocalDateTime getToday(LocalDateTime gameTime) {
        return LocalDateTime.of(gameTime.getYear(), gameTime.getMonthValue(), gameTime.getDayOfMonth(), 0, 0);
    }

    private void setTeamScore(TeamUser tu, int teamScore, Boolean isWin) {
        tu.getTeam().updateScore(teamScore, isWin);
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

    private Boolean updateScore(Game game, RankResultReqDto scoreDto, Long userId) {
        List<TeamUser> teams = teamUserRepository.findAllByGameId(game.getId());
        TeamUser myTeam = findTeamId(scoreDto.getMyTeamId(), teams);
        TeamUser enemyTeam = findTeamId(scoreDto.getEnemyTeamId(), teams);
        if (!myTeam.getUser().getId().equals(userId)) {
            throw new InvalidParameterException("team user 정보 불일치.", ErrorCode.VALID_FAILED);
        } else {
            if (myTeam.getTeam().getScore().equals(-1) && enemyTeam.getTeam().getScore().equals(-1)){
                setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
                setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
                expUpdates(game, teams);
                rankRedisService.updateRankRedis(myTeam, enemyTeam, game);
                tierService.updateAllTier(game.getSeason());
            } else {
                // score 가 이미 입력됨
                return false;
            }
            return true;
        }
    }
}