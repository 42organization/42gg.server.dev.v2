package gg.pingpong.api.admin.match.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.data.pingpong.match.type.Option;
import gg.pingpong.api.admin.match.controller.response.EnrolledMatchesResponseDto;
import gg.pingpong.api.admin.match.service.MatchAdminService;
import gg.pingpong.api.admin.match.service.dto.MatchUser;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/match")
@Validated
public class MatchAdminController {
	private final MatchAdminService matchAdminService;

	/**
	 * 매칭큐 조회
	 * @param mode : BOTH, NORMAL, RANK, 쿼리 파라미터가 없을 경우 null (전체 조회)
	 * @return
	 */
	@GetMapping
	public ResponseEntity<EnrolledMatchesResponseDto> getMatch(
		@RequestParam(name = "mode", required = false) Option mode) {
		Map<LocalDateTime, List<MatchUser>> matches = matchAdminService.getMatches(mode);
		int gameInterval = matchAdminService.getGameInterval();
		EnrolledMatchesResponseDto response = EnrolledMatchesResponseDto.of(matches, gameInterval);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
