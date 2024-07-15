package gg.agenda.api.user.agendaannouncement.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.agenda.api.user.agendaannouncement.controller.response.AgendaAnnouncementResDto;
import gg.agenda.api.user.agendaannouncement.service.AgendaAnnouncementService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/announcement")
@RequiredArgsConstructor
public class AgendaAnnouncementController {

	private final AgendaService agendaService;

	private final AgendaAnnouncementService agendaAnnouncementService;

	@PostMapping
	public ResponseEntity<Void> agendaAnnouncementAdd(@Login UserDto user, @RequestParam("agenda_key") UUID agendaKey,
		@RequestBody @Valid AgendaAnnouncementCreateReqDto agendaAnnouncementCreateReqDto) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		agendaAnnouncementService.addAgendaAnnouncement(agendaAnnouncementCreateReqDto, agenda);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// TODO: Pagination
	@GetMapping
	public ResponseEntity<List<AgendaAnnouncementResDto>> agendaAnnouncementList(
		@RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		List<AgendaAnnouncement> announcements = agendaAnnouncementService.findAllByAgenda(agenda);
		List<AgendaAnnouncementResDto> announceDto = announcements.stream()
			.map(AgendaAnnouncementResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(announceDto);
	}
}
