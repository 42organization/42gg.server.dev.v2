package gg.agenda.api.admin.agendaprofile.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.agendaprofile.controller.request.AgendaProfileAdminReqDto;
import gg.agenda.api.admin.agendaprofile.service.AgendaProfileAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/admin/profile")
public class AgendaProfileAdminController {
	private final AgendaProfileAdminService agendaProfileAdminService;

	@PatchMapping
	public ResponseEntity<Void> agendaProfileModify(@RequestParam("intra_id") String intraId,
		@RequestBody @Valid AgendaProfileAdminReqDto agendaProfileAdminReqDto) {
		agendaProfileAdminService.modifyAgendaProfile(intraId, agendaProfileAdminReqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}

