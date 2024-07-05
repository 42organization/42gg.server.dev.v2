package gg.agenda.api.user.agendaprofile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/profile")
public class AgendaProfileController {
	private final AgendaProfileFindService agendaProfileFindService;

	/**
	 * AgendaProfile 상세 조회 API
	 *
	 * @param user 로그인한 사용자 정보
	 * @return AgendaProfileDetailsResDto 객체와 HTTP 상태 코드를 포함한 ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<AgendaProfileDetailsResDto> getMyAgendaProfile(@Login UserDto user) {
		AgendaProfileDetailsResDto agendaProfileDetails = agendaProfileFindService.getAgendaProfileDetails(user);
		return ResponseEntity.status(HttpStatus.OK).body(agendaProfileDetails);
	}
}

