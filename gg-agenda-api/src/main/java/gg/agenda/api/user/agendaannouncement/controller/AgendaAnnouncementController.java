package gg.agenda.api.user.agendaannouncement.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.agenda.api.user.agendaannouncement.controller.response.AgendaAnnouncementResDto;
import gg.agenda.api.user.agendaannouncement.service.AgendaAnnouncementService;
import gg.agenda.api.utils.AgendaSlackService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/announcement")
@RequiredArgsConstructor
public class AgendaAnnouncementController {

	private final AgendaService agendaService;
	private final AgendaSlackService agendaSlackService;
	private final AgendaAnnouncementService agendaAnnouncementService;

	@PostMapping
	public ResponseEntity<Void> agendaAnnouncementAdd(@Login UserDto user, @RequestParam("agenda_key") UUID agendaKey,
		@RequestBody @Valid AgendaAnnouncementCreateReqDto agendaAnnouncementCreateReqDto) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		AgendaAnnouncement newAnnounce = agendaAnnouncementService
			.addAgendaAnnouncement(agendaAnnouncementCreateReqDto, agenda);
		agendaSlackService.slackAddAgendaAnnouncement(agenda, newAnnounce);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<PageResponseDto<AgendaAnnouncementResDto>> agendaAnnouncementList(
		@RequestParam("agenda_key") UUID agendaKey, @ModelAttribute @Valid PageRequestDto pageRequest) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaAnnouncement> announcementList = agendaAnnouncementService
			.findAnnouncementListByAgenda(pageable, agenda);

		List<AgendaAnnouncementResDto> announceDto = announcementList.stream()
			.map(AgendaAnnouncementResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<AgendaAnnouncementResDto> pageResponseDto = PageResponseDto.of(
			announcementList.getTotalElements(), announceDto);
		return ResponseEntity.ok(pageResponseDto);
	}
}
