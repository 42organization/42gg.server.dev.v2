package gg.pingpong.api.admin.game.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.game.controller.response.GameLogListAdminResponseDto;
import gg.pingpong.api.admin.game.dto.GameUserLogAdminReqDto;
import gg.pingpong.api.admin.game.dto.RankGamePPPModifyReqDto;
import gg.pingpong.api.admin.game.service.GameAdminService;
import gg.pingpong.api.global.dto.PageRequestDto;
import gg.pingpong.api.user.rank.redis.RankRedisService;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/games")
public class GameAdminController {
	private final GameAdminService gameAdminService;
	private final RankRedisService rankRedisService;

	@GetMapping
	public GameLogListAdminResponseDto gameFindBySeasonId(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		int page = pageRequestDto.getPage();
		int size = pageRequestDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
		return gameAdminService.findAllGamesByAdmin(pageable);
	}

	/**
	 * 특정 유저의 게임 목록 조회 API
	 * @param reqDto intraId page size
	 * @return GameLogListAdminResponseDto gameLogList totalPage
	 */
	@GetMapping("/users")
	public GameLogListAdminResponseDto gameFindByIntraId(@ModelAttribute GameUserLogAdminReqDto reqDto) {

		Pageable pageable = PageRequest.of(reqDto.getPage() - 1, reqDto.getSize());
		return gameAdminService.findGamesByIntraId(reqDto.getIntraId(), pageable);
	}

	/**
	 * 랭킹 점수 수정 API
	 * @param reqDto team1Id team1Score team2Id team2Score
	 * @param gameId 수정할 게임 id
	 * @return ResponseEntity<Void>
	 * @throws InvalidParameterException 점수가 3점을 초과하거나, 두 팀의 점수가 같을 경우
	 */
	@PutMapping("/{gameId}")
	public ResponseEntity<Void> gameResultEdit(@Valid @RequestBody RankGamePPPModifyReqDto reqDto,
		@PathVariable @Positive Long gameId) {
		if (reqDto.getTeam1Score() + reqDto.getTeam2Score() > 3 || reqDto.getTeam1Score() == reqDto.getTeam2Score()) {
			throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
		}
		gameAdminService.rankResultEdit(reqDto, gameId);
		rankRedisService.updateAllTier(gameId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
