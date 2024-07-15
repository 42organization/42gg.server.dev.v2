package gg.agenda.api.user.agendaannouncement.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.agenda.api.user.agendaannouncement.service.AgendaAnnouncementService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/announcement")
@RequiredArgsConstructor
public class AgendaAnnouncementController {

	private final AgendaService agendaService;

	private final AgendaAnnouncementService agendaAnnouncementService;

	@GetMapping
	public ResponseEntity<Void> agendaAnnouncementAdd(@Login UserDto user, @RequestParam("agenda_key") UUID agendaKey,
		@RequestBody @Valid AgendaAnnouncementCreateReqDto agendaAnnouncementCreateReqDto) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		agendaAnnouncementService.addAgendaAnnouncement(agendaAnnouncementCreateReqDto, agenda);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
