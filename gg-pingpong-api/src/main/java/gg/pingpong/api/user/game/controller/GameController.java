package gg.pingpong.api.user.game.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.game.type.Mode;
import gg.pingpong.api.user.game.controller.request.GameListReqDto;
import gg.pingpong.api.user.game.controller.request.NormalGameListReqDto;
import gg.pingpong.api.user.game.controller.request.NormalResultReqDto;
import gg.pingpong.api.user.game.controller.request.RankGameListReqDto;
import gg.pingpong.api.user.game.controller.request.RankResultReqDto;
import gg.pingpong.api.user.game.controller.request.TournamentResultReqDto;
import gg.pingpong.api.user.game.controller.response.GameListResDto;
import gg.pingpong.api.user.game.controller.response.GamePChangeResultResDto;
import gg.pingpong.api.user.game.dto.GameTeamInfo;
import gg.pingpong.api.user.game.service.GameFindService;
import gg.pingpong.api.user.game.service.GameService;
import gg.pingpong.api.user.rank.redis.RankRedisService;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.game.ScoreNotMatchedException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
	private final GameService gameService;
	private final GameFindService gameFindService;
	private final RankRedisService rankRedisService;

	/**
	 * 전체 게임 목록 조회 API
	 * @param gameReq - page size [status] [intraId]
	 *                  <p> status가 "LIVE" -> 진행중인 게임도 포함해서 조회 </p>
	 *                  <p>          null  -> 종료된 게임만 조회 </p>
	 * @return GameListResDto - games isLast
	 * @throws InvalidParameterException - status가 "LIVE"가 아닌데 다른 값이 들어올 경우
	 */
	@GetMapping
	GameListResDto allGameList(@Valid GameListReqDto gameReq) {
		if (gameReq.getStatus() != null && !gameReq.getStatus().name().equals("LIVE")) {
			throw new InvalidParameterException("status not valid", ErrorCode.VALID_FAILED);
		}
		Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(),
			Sort.by(Sort.Direction.DESC, "startTime"));
		if (gameReq.getIntraId() != null) {
			return gameFindService.allGameListUser(pageable, gameReq.getIntraId(),
				gameReq.getStatus() != null ? gameReq.getStatus().name() : "NULL");
		}
		return gameFindService.allGameList(pageable, gameReq.getStatus() != null ? gameReq.getStatus().name() : "NULL");
	}

	@GetMapping("/normal")
	GameListResDto normalGameList(@ModelAttribute @Valid NormalGameListReqDto gameReq) {
		Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(),
			Sort.by(Sort.Direction.DESC, "startTime"));
		if (gameReq.getIntraId() == null) {
			return gameFindService.getNormalGameList(pageable);
		}
		return gameFindService.normalGameListByIntra(pageable, gameReq.getIntraId());
	}

	@GetMapping("/rank")
	GameListResDto rankGameList(@ModelAttribute @Valid RankGameListReqDto gameReq) {
		Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(),
			Sort.by(Sort.Direction.DESC, "startTime"));
		if (gameReq.getIntraId() == null) {
			return gameFindService.rankGameList(pageable, gameReq.getSeasonId());
		}
		return gameFindService.rankGameListByIntra(pageable, gameReq.getSeasonId(), gameReq.getIntraId());
	}

	@GetMapping("/{gameId}")
	GameTeamInfo getGameInfo(@PathVariable Long gameId, @Parameter(hidden = true) @Login UserDto userDto) {
		return gameService.getUserGameInfo(gameId, userDto.getId());
	}

	@PostMapping("/rank")
	synchronized ResponseEntity<Void> createRankResult(@Valid @RequestBody RankResultReqDto reqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		if (reqDto.getMyTeamScore() + reqDto.getEnemyTeamScore() > 3
			|| reqDto.getMyTeamScore() + reqDto.getEnemyTeamScore() < 2
			|| reqDto.getMyTeamScore() == reqDto.getEnemyTeamScore()) {
			throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
		}
		if (!gameService.createRankResult(reqDto, user.getId())) {
			throw new ScoreNotMatchedException();
		}
		rankRedisService.updateAllTier(reqDto.getGameId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/normal")
	ResponseEntity<Void> createNormalResult(@Valid @RequestBody NormalResultReqDto reqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		if (gameService.normalExpResult(reqDto, user.getId())) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	/**
	 * 토너먼트 게임 결과 등록
	 * @param reqDto 요청 Dto
	 * @param user 사용자
	 * @exception InvalidParameterException 유효하지 않은 점수 입력할 경우
	 * @return 201 created
	 */
	@PostMapping("/tournament")
	synchronized ResponseEntity<Void> createTournamentGameResult(@Valid @RequestBody TournamentResultReqDto reqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		if (reqDto.getMyTeamScore() + reqDto.getEnemyTeamScore() > 3
			|| reqDto.getMyTeamScore() + reqDto.getEnemyTeamScore() < 2
			|| reqDto.getMyTeamScore() == reqDto.getEnemyTeamScore()) {
			throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
		}
		gameService.createTournamentGameResult(reqDto, user.getId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * RANK, NORMAL, TOURNAMENT 게임 결과 반환.
	 */
	@GetMapping("/{gameId}/pchange/result")
	ResponseEntity<GamePChangeResultResDto> getGamePChangeResult(@PathVariable Long gameId,
		@Parameter(hidden = true) @Login UserDto user, @RequestParam Mode mode) {
		if (mode == Mode.RANK) {
			return ResponseEntity.ok(gameService.pppChangeResult(gameId, user.getId()));
		} else if (mode == Mode.NORMAL || mode == Mode.TOURNAMENT) {
			return ResponseEntity.ok(gameService.expChangeResult(gameId, user.getId()));
		}
		throw new BusinessException(ErrorCode.BAD_ARGU);
	}
}
