package gg.agenda.api.user.agendaprofile.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendaprofile.controller.request.AgendaProfileChangeReqDto;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/profile")
public class AgendaProfileController {
	private final AgendaProfileFindService agendaProfileFindService;
	private final AgendaProfileService agendaProfileService;
	private static final Logger log = LoggerFactory.getLogger(AgendaProfileController.class);

	/**
	 * AgendaProfile 상세 조회 API
	 * @param user 로그인한 사용자 정보
	 * @return AgendaProfileDetailsResDto 객체와 HTTP 상태 코드를 포함한 ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<AgendaProfileDetailsResDto> myAgendaProfileDetails(
		@Login @Parameter(hidden = true) UserDto user) {
		AgendaProfileDetailsResDto agendaProfileDetails = agendaProfileFindService.detailsAgendaProfile(user.getId());
		return ResponseEntity.status(HttpStatus.OK).body(agendaProfileDetails);
	}

	/**
	 * AgendaProfile 변경 API
	 * @param user  로그인한 사용자 정보
	 * @param reqDto 변경할 프로필 정보
	 * @return HTTP 상태 코드와 빈 응답
	 */
	@PatchMapping
	public ResponseEntity<Void> agendaProfileModify(@Login @Parameter(hidden = true) UserDto user,
		@RequestBody @Valid AgendaProfileChangeReqDto reqDto) {
		agendaProfileService.modifyAgendaProfile(user.getIntraId(), reqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}

