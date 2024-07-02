package gg.agenda.api.user.agenda.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.controller.dto.AgendaResponseDto;
import gg.agenda.api.user.agenda.service.AgendaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	@GetMapping
	public ResponseEntity<AgendaResponseDto> agendaDetails(@RequestParam("agenda_id") UUID agendaKey) {
		AgendaResponseDto agendaDto = agendaService.findAgendaWithLatestAnnouncement(agendaKey);
		return ResponseEntity.ok(agendaDto);
	}
}
