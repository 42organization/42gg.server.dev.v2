package gg.agenda.api.admin.agendaannouncement.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.agendaannouncement.controller.request.AgendaAnnouncementAdminUpdateReqDto;
import gg.agenda.api.admin.agendaannouncement.controller.response.AgendaAnnouncementAdminResDto;
import gg.agenda.api.admin.agendaannouncement.service.AgendaAnnouncementAdminService;
import gg.data.agenda.AgendaAnnouncement;
import gg.utils.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/admin/announcement")
@RequiredArgsConstructor
public class AgendaAnnouncementAdminController {

	private final AgendaAnnouncementAdminService agendaAnnouncementAdminService;

	@GetMapping()
	public ResponseEntity<List<AgendaAnnouncementAdminResDto>> agendaAnnouncementList(
		@RequestParam("agenda_key") UUID agendaKey, @ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
		List<AgendaAnnouncement> announcements = agendaAnnouncementAdminService
			.getAgendaAnnouncementList(agendaKey, pageable);
		List<AgendaAnnouncementAdminResDto> announceDtos = announcements.stream()
			.map(AgendaAnnouncementAdminResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(announceDtos);
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAgendaAnnouncement(
		@RequestBody @Valid AgendaAnnouncementAdminUpdateReqDto updateReqDto) {
		agendaAnnouncementAdminService.updateAgendaAnnouncement(updateReqDto);
		return ResponseEntity.noContent().build();
	}
}
