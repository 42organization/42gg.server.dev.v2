package gg.agenda.api.admin.agendaprofile.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.agendaprofile.controller.request.AgendaProfileChangeAdminReqDto;
import gg.agenda.api.admin.agendaprofile.service.AgendaProfileAdminService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/admin/profile")
public class AgendaProfileAdminController {
	private final AgendaProfileAdminService agendaProfileAdminService;

	/**
	 * 관리자 개인 프로필 변경 API
	 *
	 * @param user    로그인한 사용자 정보
	 * @param intraId 수정할 사용자의 intra_id
	 * @param reqDto  변경할 프로필 정보
	 * @return HTTP 상태 코드와 빈 응답
	 */
	@PatchMapping
	public ResponseEntity<String> agendaProfileModify(@Login @Parameter(hidden = true) UserDto user,
		@RequestParam String intraId,
		@RequestBody @Valid AgendaProfileChangeAdminReqDto reqDto) {
		agendaProfileAdminService.modifyAgendaProfile(intraId, reqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}

