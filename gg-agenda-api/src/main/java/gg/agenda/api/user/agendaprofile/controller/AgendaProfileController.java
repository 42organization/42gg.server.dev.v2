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
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileInfoDetailsResDto;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.user.type.RoleType;
import gg.repo.user.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/profile")
public class AgendaProfileController {
	private final AgendaProfileFindService agendaProfileFindService;
	private final AgendaProfileService agendaProfileService;
	private final UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(AgendaProfileController.class);

	/**
	 * AgendaProfile 상세 조회 API
	 * @param user 로그인한 사용자 정보
	 * @return AgendaProfileDetailsResDto 객체와 HTTP 상태 코드를 포함한 ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<AgendaProfileDetailsResDto> myAgendaProfileDetails(
		@Login @Parameter(hidden = true) UserDto user) {
		AgendaProfileDetailsResDto agendaProfileDetails = agendaProfileFindService.detailsAgendaProfile(
			user.getIntraId());
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
		agendaProfileService.modifyAgendaProfile(user.getId(), reqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * AgendaProfile 상세 조회 API
	 * @param user 로그인한 사용자 정보
	 * @return AgendaProfileDetailsResDto 객체와 HTTP 상태 코드를 포함한 ResponseEntity
	 */
	@GetMapping("/info")
	public ResponseEntity<AgendaProfileInfoDetailsResDto> myAgendaProfileInfoDetails(
		@Login @Parameter(hidden = true) UserDto user) {
		String intraId = user.getIntraId();
		Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;

		AgendaProfileInfoDetailsResDto agendaProfileInfoDetails = new AgendaProfileInfoDetailsResDto(intraId, isAdmin);

		return ResponseEntity.ok(agendaProfileInfoDetails);
	}

	// /**
	//  * 현재 참여중인 Agenda 목록 조회하는 메서드
	//  * @param user 로그인한 유저의 id
	//  * @return List<CurrentAttendAgendaListResDto> 객체
	//  */
	// @GetMapping("/current/list")
	// public ResponseEntity<List<CurrentAttendAgendaListResDto>> getCurrentAttendAgendaList(
	// 	@Login @Parameter(hidden = true) UserDto user) {
	//
	// 	List<CurrentAttendAgendaListResDto> currentAttendAgendaList = agendaProfileFindService.findCurrentAttendAgenda(
	// 		user.getId());
	// 	return ResponseEntity.ok(currentAttendAgendaList);
	// }
}

