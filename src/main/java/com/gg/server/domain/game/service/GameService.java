package com.gg.server.domain.game.service;

import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.coin.service.UserCoinChangeService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.*;
import com.gg.server.domain.game.dto.request.NormalResultReqDto;
import com.gg.server.domain.game.dto.request.RankResultReqDto;
import com.gg.server.domain.game.dto.request.TournamentResultReqDto;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.game.exception.GameStatusNotMatchedException;
import com.gg.server.domain.game.exception.ScoreAlreadyEnteredException;
import com.gg.server.domain.match.service.MatchTournamentService;
import com.gg.server.domain.match.type.TournamentMatch;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.team.exception.TeamIdNotMatchException;
import com.gg.server.domain.tier.service.TierService;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
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
    private final TournamentGameRepository tournamentGameRepository;
    private final MatchTournamentService matchTournamentService;
    private final TournamentGameRepository tournamentGameRepository;

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

    /**
     * rank 게임 결과를 입력한다.
     * WAIT, LIVE 상태일 때만 입력 가능
     * @return Boolean 입력 성공 여부
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "rankGameListByIntra", allEntries = true),
            @CacheEvict(value = "rankGameList", allEntries = true),
            @CacheEvict(value = "allGameList", allEntries = true),
            @CacheEvict(value = "allGameListByUser", allEntries = true),
            @CacheEvict(value = "ranking", allEntries = true),
            @CacheEvict(value = "expRanking", allEntries = true)
    })
    public Boolean createRankResult(RankResultReqDto scoreDto, Long userId) {
        // 현재 게임 id
        Game game = gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId());
        if (game.getStatus() != StatusType.WAIT && game.getStatus() != StatusType.LIVE) {
            return false;
        }
        return updateRankGameScore(game, scoreDto, userId);
    }

    /**
     * tournament 게임 결과 등록
     * @param scoreDto 요청 Dto
     * @param userId 사용자 Id
     * @exception GameStatusNotMatchedException 게임 상태가 WAIT, LIVE가 아닐 경우
     * @return Boolean 입력 성공 여부
     */
    @Transactional
    public void createTournamentGameResult(TournamentResultReqDto scoreDto, Long userId) {
        Game game = gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId());
        if (game.getStatus() != StatusType.WAIT && game.getStatus() != StatusType.LIVE) {
            throw new GameStatusNotMatchedException();
        }
        updateTournamentGameScore(game, scoreDto, userId);
        if (TournamentMatch.POSSIBLE.equals(matchTournamentService.checkTournamentGame(game))) {
            TournamentGame tournamentGame = tournamentGameRepository.findByGameId(game.getId())
                    .orElseThrow(GameNotExistException::new);
            Tournament tournament = tournamentGame.getTournament();
            matchTournamentService.matchGames(tournament, tournamentGame.getTournamentRound().getNextRound());
        }
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
            @CacheEvict(value = "allGameListByUser", allEntries = true),
            @CacheEvict(value = "ranking", allEntries = true),
            @CacheEvict(value = "expRanking", allEntries = true)
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

    private Boolean updateRankGameScore(Game game, RankResultReqDto scoreDto, Long userId) {
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

    /**
     * 토너먼트 게임 결과 업데이트 메소드
     * @param game
     * @param scoreDto
     * @param userId
     * @exception InvalidParameterException 파라미터로 받은 userId가 myTeam의 userId와 일치하지 않을 경우
     * @exception ScoreAlreadyEnteredException 게임 점수가 이미 작성되어 있을 경우
     */
    private void updateTournamentGameScore(Game game, TournamentResultReqDto scoreDto, Long userId) {
        List<TeamUser> teams = teamUserRepository.findAllByGameId(game.getId());
        TeamUser myTeam = findTeamId(scoreDto.getMyTeamId(), teams);
        TeamUser enemyTeam = findTeamId(scoreDto.getEnemyTeamId(), teams);
        if (!myTeam.getUser().getId().equals(userId)) {
            throw new InvalidParameterException("team user 정보가 일치하지 않습니다.", ErrorCode.VALID_FAILED);
        }
        if (myTeam.getTeam().getScore().equals(-1) && enemyTeam.getTeam().getScore().equals(-1)){
            setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
            setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(), scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
            expUpdates(game, teams);
        } else {
            // score 가 이미 입력됨
            throw new ScoreAlreadyEnteredException(ErrorCode.SCORE_ALREADY_ENTERED.getMessage(), ErrorCode.SCORE_ALREADY_ENTERED);
        }
    }
}