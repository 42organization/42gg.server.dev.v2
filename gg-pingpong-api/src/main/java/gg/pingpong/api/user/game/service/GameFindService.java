package gg.pingpong.api.user.game.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.game.type.StatusType;
import gg.pingpong.api.user.game.controller.response.GameListResDto;
import gg.pingpong.api.user.game.controller.response.GameResultResDto;
import gg.repo.game.GameRepository;
import gg.repo.game.out.GameTeamUser;
import gg.utils.exception.game.GameNotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameFindService {
	private final GameRepository gameRepository;

	/**
	 * 특정 User의 NORMAL 모드의 END 상태의 게임 목록 조회
	 * @param pageable
	 * @param intra - 조회할 유저의 intraId
	 * @return GameListResDto - games isLast
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "normalGameListByIntra", cacheManager = "gameCacheManager")
	public GameListResDto normalGameListByIntra(Pageable pageable, String intra) {
		Slice<Long> games = gameRepository.findGamesByUserAndModeAndStatus(intra, Mode.NORMAL.name(),
			StatusType.END.name(), pageable);
		return new GameListResDto(getNormalGameResultList(games.getContent()), games.isLast());
	}

	/**
	 * NORMAL 모드의 END 상태의 게임 목록 조회
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "normalGameList", cacheManager = "gameCacheManager")
	public GameListResDto getNormalGameList(Pageable pageable) {
		Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
		return new GameListResDto(
			getNormalGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.isLast());
	}

	/**
	 * 특정 User의 RANK 모드의 END 상태의 게임 목록 조회
	 * @param pageable
	 * @param seasonId - 조회할 시즌의 id
	 * @param intra - 조회할 유저의 intraId
	 * @return GameListResDto - games isLast
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "rankGameListByIntra", cacheManager = "gameCacheManager")
	public GameListResDto rankGameListByIntra(Pageable pageable, Long seasonId, String intra) {
		Slice<Long> games = gameRepository.findGamesByUserAndModeAndSeason(intra, Mode.RANK.name(), seasonId,
			StatusType.END.name(), pageable);
		return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
	}

	/**
	 * RANK 모드의 END 상태의 게임 목록 조회
	 * @param pageable
	 * @param seasonId - 조회할 시즌의 id
	 * @return GameListResDto - games isLast
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "rankGameList", cacheManager = "gameCacheManager")
	public GameListResDto rankGameList(Pageable pageable, Long seasonId) {
		Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId,
			pageable);
		return new GameListResDto(
			getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.isLast());
	}

	/**
	 * NORMAL, RANDOM 모드의 게임 목록 조회
	 * <p>
	 * status가 "LIVE" -> 진행중인 게임도 포함해서 조회 (END, LIVE, WAIT) <br>
	 * status가 null -> 종료된 게임만 조회 (END)
	 * </p>
	 * @param pageable
	 * @param status - "LIVE" or null
	 * @return GameListResDto - games isLast
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "allGameList",
		cacheManager = "gameCacheManager",
		key = "#pageable.pageNumber + #pageable.pageSize + #pageable.sort.toString() + #status")
	public GameListResDto allGameList(Pageable pageable, String status) {
		Slice<Game> games;
		if (status != null && status.equals("LIVE")) {
			games = gameRepository.findAllByModeInAndStatusIn(Arrays.asList(Mode.RANK, Mode.NORMAL),
				Arrays.asList(StatusType.END, StatusType.LIVE, StatusType.WAIT), pageable);
		} else {
			games = gameRepository.findAllByModeInAndStatus(Arrays.asList(Mode.RANK, Mode.NORMAL), StatusType.END,
				pageable);
		}
		return new GameListResDto(
			getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.isLast());
	}

	/**
	 * allGameList()와 동일한 로직 + intraId로 조회
	 * @param pageable
	 * @param intra - 조회할 intraId
	 * @param status - "LIVE" or null
	 * @return GameListResDto - games isLast
	 * @throws GameNotExistException - intraId로 조회한 게임이 없을 경우
	 */
	@Transactional(readOnly = true)
	@Cacheable(value = "allGameListByUser", cacheManager = "gameCacheManager",
		key = "#pageable.pageNumber + #pageable.pageSize + #pageable.sort.toString() + #status + #intra")
	public GameListResDto allGameListUser(Pageable pageable, String intra, String status) {
		List<String> statusTypes = new ArrayList<>(List.of(StatusType.END.name()));
		if (status != null && status.equals("LIVE")) {
			statusTypes.add(StatusType.LIVE.name());
			statusTypes.add(StatusType.WAIT.name());
		}
		Slice<Long> games = gameRepository.findGamesByUserAndModeInAndStatusIn(intra,
			Arrays.asList(Mode.RANK.name(), Mode.NORMAL.name()), statusTypes, pageable);
		return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
	}

	public Game findByGameId(Long gameId) {
		return gameRepository.findById(gameId)
			.orElseThrow(GameNotExistException::new);
	}

	public Game findGameWithPessimisticLockById(Long id) {
		return gameRepository.findWithPessimisticLockById(id)
			.orElseThrow(GameNotExistException::new);
	}

	/**
	 * private method
	 */
	private List<GameResultResDto> getGameResultList(List<Long> games) {
		List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games);
		return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
	}

	private List<GameResultResDto> getNormalGameResultList(List<Long> games) {
		List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsInAndNormalMode(games);
		return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
	}
}
