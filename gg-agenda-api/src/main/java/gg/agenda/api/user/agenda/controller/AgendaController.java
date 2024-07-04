package gg.agenda.api.user.agenda.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.controller.dto.AgendaResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaSimpleResponseDto;
import gg.agenda.api.user.agenda.service.AgendaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Agenda 상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "Agenda 조회 요청이 잘못됨"),
		@ApiResponse(responseCode = "404", description = "Agenda를 찾을 수 없음")
	})
	@GetMapping
	public ResponseEntity<AgendaResponseDto> agendaDetails(@RequestParam("agenda_key") UUID agendaKey) {
		AgendaResponseDto agendaDto = agendaService.findAgendaWithLatestAnnouncement(agendaKey);
		return ResponseEntity.ok(agendaDto);
	}

	@ApiResponse(responseCode = "200", description = "현재 진행중인 Agenda 목록 조회 성공")
	@GetMapping("/list")
	public ResponseEntity<List<AgendaSimpleResponseDto>> agendaListCurrent() {
		List<AgendaSimpleResponseDto> agendaList = agendaService.findCurrentAgendaList();
		return ResponseEntity.ok(agendaList);
	}
}
