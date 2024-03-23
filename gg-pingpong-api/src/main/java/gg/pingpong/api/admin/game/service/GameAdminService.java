package gg.pingpong.api.admin.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.game.GameAdminRepository;
import gg.admin.repo.game.PChangeAdminRepository;
import gg.admin.repo.game.TeamUserAdminRepository;
import gg.admin.repo.game.out.GameTeamUser;
import gg.admin.repo.season.SeasonAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.PChange;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.rank.redis.RankRedis;
import gg.data.pingpong.season.Season;
import gg.data.user.User;
import gg.pingpong.api.admin.game.controller.response.GameLogListAdminResponseDto;
import gg.pingpong.api.admin.game.dto.GameLogAdminDto;
import gg.pingpong.api.admin.game.dto.RankGamePPPModifyReqDto;
import gg.pingpong.api.user.rank.redis.RankRedisService;
import gg.pingpong.api.user.rank.service.TierService;
import gg.pingpong.api.user.season.dto.CurSeason;
import gg.pingpong.api.user.season.service.SeasonService;
import gg.repo.game.PChangeRepository;
import gg.repo.match.RedisMatchUserRepository;
import gg.utils.exception.game.GameNotExistException;
import gg.utils.exception.game.NotRecentlyGameException;
import gg.utils.exception.pchange.PChangeNotExistException;
import gg.utils.exception.season.SeasonNotFoundException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameAdminService {

	private final GameAdminRepository gameAdminRepository;
	private final SeasonAdminRepository seasonAdminRepository;
	private final UserAdminRepository userAdminRepository;
	private final PChangeRepository pChangeRepository;
	private final PChangeAdminRepository pChangeAdminRepository;
	private final RankRedisService rankRedisService;
	private final TeamUserAdminRepository teamUserAdminRepository;
	private final RedisMatchUserRepository redisMatchUserRepository;
	private final TierService tierService;
	private final SeasonService seasonService;
	private final EntityManager entityManager;

	/**
	 * <p>토너먼트 게임을 제외한 일반, 랭크 게임들을 찾아서 반환해준다.</p>
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public GameLogListAdminResponseDto findAllGamesByAdmin(Pageable pageable) {
		Page<Game> gamePage = gameAdminRepository.findAllByModeIn(pageable, List.of(Mode.NORMAL, Mode.RANK));
		return new GameLogListAdminResponseDto(
			getGameLogList(gamePage.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			gamePage.getTotalPages());
	}

	/**
	 * <p>토너먼트 게임을 제외한 해당 시즌의 일반, 랭크 게임들을 찾아서 반환해준다.</p>
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public GameLogListAdminResponseDto findGamesBySeasonId(Long seasonId, Pageable pageable) {
		Season season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);
		Page<Game> games = gameAdminRepository.findBySeasonAndModeIn(pageable, season, List.of(Mode.NORMAL, Mode.RANK));
		return new GameLogListAdminResponseDto(
			getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.getTotalPages());
	}

	@Transactional(readOnly = true)
	public List<GameLogAdminDto> getGameLogList(List<Long> gameIdList) {
		List<GameTeamUser> teamViews = gameAdminRepository.findTeamsByGameIsIn(gameIdList);
		return teamViews.stream().map(GameLogAdminDto::new).collect(Collectors.toList());
	}

	/**
	 * 특정 유저의 게임 목록 조회 (토너먼트 게임 제외)
	 * @param intraId 조회할 유저의 intraId
	 * @param pageable page size
	 * @return GameLogListAdminResponseDto
	 * @throws UserNotFoundException intraId에 해당하는 유저가 없을 경우
	 */
	@Transactional(readOnly = true)
	public GameLogListAdminResponseDto findGamesByIntraId(String intraId, Pageable pageable) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		List<PChange> pChangeList = pChangeRepository.findAllByUserIdGameModeIn(user.getId(),
			List.of(Mode.NORMAL, Mode.RANK));
		List<Game> gameList = new ArrayList<>();

		for (PChange pChange : pChangeList) {
			gameList.add(pChange.getGame());
		}

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), gameList.size());
		Page<Game> games = new PageImpl<>(gameList.subList(start, end), pageable, gameList.size());
		return new GameLogListAdminResponseDto(
			getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.getTotalPages());
	}

	/**
	 * 랭킹 점수 수정
	 * @param reqDto team1Id team1Score team2Id team2Score
	 * @param gameId 수정할 게임 id
	 * @throws GameNotExistException gameId에 해당하는 게임이 없을 경우
	 * @throws SeasonNotFoundException 게임에 해당하는 시즌이 없을 경우
	 * @throws NotRecentlyGameException 게임이 두명 다 가장 마지막 게임이 아닐 경우
	 */
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "rankGameListByIntra", allEntries = true),
		@CacheEvict(value = "rankGameList", allEntries = true),
		@CacheEvict(value = "allGameList", allEntries = true),
		@CacheEvict(value = "allGameListByUser", allEntries = true),
		@CacheEvict(value = "ranking", allEntries = true)
	})
	public void rankResultEdit(RankGamePPPModifyReqDto reqDto, Long gameId) {
		// 게임이 두명 다 가장 마지막 게임인지 확인 (그 game에 해당하는 팀이 맞는지 확인)
		List<TeamUser> teamUsers = teamUserAdminRepository.findUsersByTeamIdIn(
			List.of(reqDto.getTeam1Id(), reqDto.getTeam2Id()));
		Game game = gameAdminRepository.findGameWithSeasonByGameId(gameId)
			.orElseThrow(GameNotExistException::new);
		CurSeason curSeason = seasonService.getCurSeason();
		if (!isRecentlyGame(teamUsers, gameId) || enrollSlots(teamUsers) || !game.getSeason()
			.getId()
			.equals(curSeason.getId())) {
			throw new NotRecentlyGameException();
		}
		// pchange 가져와서 rank ppp 이전 값을 가지고 새 점수를 바탕으로 다시 계산
		// user 1
		List<PChange> pChanges = pChangeAdminRepository.findByTeamUser(teamUsers.get(0).getUser().getId());
		if (!pChanges.get(0).getGame().getId().equals(gameId)) {
			throw new PChangeNotExistException();
		}
		RankRedis rankRedis1 = rollbackGameResult(game.getSeason(), teamUsers.get(0), pChanges);
		pChangeAdminRepository.deleteById(pChanges.get(0).getId());
		// user 2
		pChanges = pChangeAdminRepository.findByTeamUser(teamUsers.get(1).getUser().getId());
		if (!pChanges.get(0).getGame().getId().equals(gameId)) {
			throw new PChangeNotExistException();
		}
		RankRedis rankRedis2 = rollbackGameResult(game.getSeason(), teamUsers.get(1), pChanges);
		pChangeAdminRepository.deleteById(pChanges.get(0).getId());
		for (int i = 0; i < teamUsers.size(); i++) {
			updateScore(reqDto, teamUsers.get(i));
		}
		rankRedisService.updateAdminRankData(teamUsers.get(0), teamUsers.get(1), game, rankRedis1, rankRedis2);
		tierService.updateAllTier(game.getSeason());
	}

	private RankRedis rollbackGameResult(Season season, TeamUser teamUser, List<PChange> pChanges) {
		// pchange ppp도 update
		// rankredis 에 ppp 다시 반영
		// rank zset 도 update
		// 이전 ppp, exp 되돌리기
		// rank data 에 있는 ppp 되돌리기
		RankRedis rankRedis;
		if (pChanges.size() == 1) {
			rankRedis = rankRedisService.rollbackRank(teamUser, season.getStartPpp(), season.getId());
			teamUser.getUser().updateExp(0);
		} else {
			rankRedis = rankRedisService.rollbackRank(teamUser, pChanges.get(1).getPppResult(), season.getId());
			teamUser.getUser().updateExp(pChanges.get(1).getExp());
		}
		return rankRedis;
	}

	private void updateScore(RankGamePPPModifyReqDto reqDto, TeamUser teamUser) {
		if (teamUser.getTeam().getId().equals(reqDto.getTeam1Id())) {
			teamUser.getTeam().updateScore(reqDto.getTeam1Score(), reqDto.getTeam1Score() > reqDto.getTeam2Score());
		} else if (teamUser.getTeam().getId().equals(reqDto.getTeam2Id())) {
			teamUser.getTeam().updateScore(reqDto.getTeam2Score(), reqDto.getTeam2Score() > reqDto.getTeam1Score());
		}
	}

	private Boolean isRecentlyGame(List<TeamUser> teamUsers, Long gameId) {
		for (TeamUser teamUser : teamUsers) {
			List<PChange> pChanges = pChangeAdminRepository.findByTeamUser(teamUser.getUser().getId());
			if (!pChanges.get(0).getGame().getId().equals(gameId)) {
				return false;
			}
		}
		return true;
	}

	private Boolean enrollSlots(List<TeamUser> teamUsers) {
		for (TeamUser teamUser : teamUsers) {
			Long userId = teamUser.getUser().getId();
			if (redisMatchUserRepository.countMatchTime(userId) > 0) {
				return true;
			}
			if (gameAdminRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userId).isPresent()) {
				return true;
			}
		}
		return false;
	}
}
