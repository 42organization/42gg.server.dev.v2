package gg.party.api.user.penalty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.party.api.user.penalty.PenaltyService;
import gg.party.api.user.penalty.controller.response.PenaltyResDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/penalty")
public class PenaltyController {
	private final PenaltyService penaltyService;

	/**
	 * 현재 유저가 패널티 상태인지 조회
	 * @return 언제까지 패널티인지 DateTime으로 리턴
	 */
	@GetMapping
	public ResponseEntity<PenaltyResDto> findPenalty(@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.status(HttpStatus.OK).body(penaltyService.findIsPenalty(user));
	}
}
