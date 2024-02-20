package gg.pingpong.api.user.match.controller;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.data.match.type.Option;
import com.gg.server.domain.match.dto.MatchRequestDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.dto.SlotStatusResponseListDto;
import com.gg.server.domain.match.service.MatchFindService;
import com.gg.server.domain.match.service.MatchService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/match")
public class MatchController {
	private final MatchService matchService;
	private final MatchFindService matchFindService;

	/**
	 * 유저 슬롯 입장 요청 API (== 매칭 요청 API)
	 * @param matchRequestDto
	 * @param user 매칭 요청한 유저
	 * @return 201 (Created)
	 */
	@PostMapping
	public ResponseEntity createUserMatch(@RequestBody @Valid MatchRequestDto matchRequestDto,
		@Parameter(hidden = true) @Login UserDto user) {
		matchService.makeMatch(user, matchRequestDto.getOption(), matchRequestDto.getStartTime());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping
	public ResponseEntity deleteUserMatch(
		@RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
		@Parameter(hidden = true) @Login UserDto user) {
		matchService.cancelMatch(user, startTime);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 특정 시간대의 경기 매칭 가능 상태 조회 API
	 * @param mode : BOTH, NORMAL, RANK
	 * @param user
	 * @return
	 */
	@GetMapping("/time/scope")
	public SlotStatusResponseListDto getMatchTimeScope(@RequestParam(required = true) String mode,
		@Parameter(hidden = true) @Login UserDto user) {
		return matchFindService.getAllMatchStatus(user, Option.getEnumValue(mode));
	}

	@GetMapping
	public MatchStatusResponseListDto getCurrentMatch(@Parameter(hidden = true) @Login UserDto user) {
		return matchFindService.getCurrentMatch(user);
	}

}
