package gg.pingpong.api.user.game.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.PChange;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.match.type.TournamentMatchStatus;
import gg.data.pingpong.season.Season;
import gg.data.pingpong.tournament.Tournament;
import gg.data.pingpong.tournament.TournamentGame;
import gg.data.pingpong.tournament.type.RoundNumber;
import gg.pingpong.api.global.utils.ExpLevelCalculator;
import gg.pingpong.api.user.game.controller.request.NormalResultReqDto;
import gg.pingpong.api.user.game.controller.request.RankResultReqDto;
import gg.pingpong.api.user.game.controller.request.TournamentResultReqDto;
import gg.pingpong.api.user.game.controller.response.GamePChangeResultResDto;
import gg.pingpong.api.user.game.controller.response.GamePPPChangeResultResDto;
import gg.pingpong.api.user.game.dto.GameTeamInfo;
import gg.pingpong.api.user.match.service.MatchTournamentService;
import gg.pingpong.api.user.rank.redis.RankRedisService;
import gg.pingpong.api.user.rank.service.TierService;
import gg.pingpong.api.user.store.dto.UserGameCoinResultDto;
import gg.pingpong.api.user.store.service.UserCoinChangeService;
import gg.repo.game.GameRepository;
import gg.repo.game.PChangeRepository;
import gg.repo.game.TeamUserRepository;
import gg.repo.game.out.GameTeamUserInfo;
import gg.repo.tournarment.TournamentGameRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.game.GameNotExistException;
import gg.utils.exception.game.GameStatusNotMatchedException;
import gg.utils.exception.game.ScoreAlreadyEnteredException;
import gg.utils.exception.pchange.PChangeNotExistException;
import gg.utils.exception.team.TeamIdNotMatchException;
import gg.utils.exception.tournament.TournamentGameNotFoundException;
import lombok.RequiredArgsConstructor;

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
		if (TournamentMatchStatus.REQUIRED.equals(matchTournamentService.checkTournamentGame(game))) {
			TournamentGame tournamentGame = tournamentGameRepository.findByGameId(game.getId())
				.orElseThrow(TournamentGameNotFoundException::new);
			Tournament tournament = tournamentGame.getTournament();
			RoundNumber matchRound = tournamentGame.getTournamentRound().getNextRound().getRoundNumber();
			matchTournamentService.matchGames(tournament, matchRound);
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
		if (teamUsers.size() == 2
			&& (game.getStatus() == StatusType.WAIT || game.getStatus() == StatusType.LIVE)) {
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
	 * @return GamePChangeResultResDto 경험치 변화 결과
	 */
	@Transactional
	public GamePChangeResultResDto expChangeResult(Long gameId, Long userId) {
		List<PChange> pChanges = pChangeService.findExpChangeHistory(gameId, userId);
		UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addNormalGameCoin(userId);

		if (pChanges.size() == 1) {
			return new GamePChangeResultResDto(0, pChanges.get(0).getExp(), userGameCoinResultDto);
		} else {
			return new GamePChangeResultResDto(pChanges.get(1).getExp(), pChanges.get(0).getExp(),
				userGameCoinResultDto);
		}
	}

	/**
	 * rank 게임에 대한 exp, ppp 변화 결과를 가져온다.
	 * @param gameId 게임 id
	 * @param userId 게임에 참여한 유저 id
	 * @return GamePPPChangeResultResDto 경험치 변화 결과
	 * @throws PChangeNotExistException
	 */
	@Transactional
	public GamePPPChangeResultResDto pppChangeResult(Long gameId, Long userId) throws PChangeNotExistException {
		Season season = gameFindService.findByGameId(gameId).getSeason();
		List<PChange> pppHistory = pChangeService.findPPPChangeHistory(gameId, userId, season.getId());
		List<PChange> expHistory = pChangeService.findExpChangeHistory(gameId, userId);
		UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addRankGameCoin(gameId, userId);
		return new GamePPPChangeResultResDto(expHistory.size() <= 1 ? 0 : expHistory.get(1).getExp(),
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
			.ifPresentOrElse(
				pChange -> {
					pChange.checkPChange();
					pChangeRepository.save(pChange);
				},
				() -> {
					throw new PChangeNotExistException();
				});
	}

	public void savePChange(Game game, List<TeamUser> teamUsers, Long loginUserId) {
		if (!pChangeRepository.findPChangesByGameId(game.getId()).isEmpty()) {
			return;
		}
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
			if (myTeam.getTeam().getScore().equals(-1) && enemyTeam.getTeam().getScore().equals(-1)) {
				setTeamScore(myTeam, scoreDto.getMyTeamScore(),
					scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
				setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(),
					scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
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
		if (myTeam.getTeam().getScore().equals(-1) && enemyTeam.getTeam().getScore().equals(-1)) {
			setTeamScore(myTeam, scoreDto.getMyTeamScore(), scoreDto.getMyTeamScore() > scoreDto.getEnemyTeamScore());
			setTeamScore(enemyTeam, scoreDto.getEnemyTeamScore(),
				scoreDto.getMyTeamScore() < scoreDto.getEnemyTeamScore());
			expUpdates(game, teams);
			savePChange(game, teams, userId);
		} else {
			// score 가 이미 입력됨
			throw new ScoreAlreadyEnteredException(ErrorCode.SCORE_ALREADY_ENTERED.getMessage(),
				ErrorCode.SCORE_ALREADY_ENTERED);
		}
	}
}
