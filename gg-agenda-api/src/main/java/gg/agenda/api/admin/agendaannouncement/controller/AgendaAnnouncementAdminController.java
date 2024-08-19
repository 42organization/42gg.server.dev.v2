package gg.agenda.api.admin.agendaannouncement.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
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
import gg.utils.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/admin/announcement")
@RequiredArgsConstructor
public class AgendaAnnouncementAdminController {

	private final AgendaAnnouncementAdminService agendaAnnouncementAdminService;

	@GetMapping()
	public ResponseEntity<PageResponseDto<AgendaAnnouncementAdminResDto>> agendaAnnouncementList(
		@RequestParam("agenda_key") UUID agendaKey, @ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaAnnouncement> agendaAnnouncementList = agendaAnnouncementAdminService
			.getAgendaAnnouncementList(agendaKey, pageable);

		List<AgendaAnnouncementAdminResDto> announceDtos = agendaAnnouncementList.getContent().stream()
			.map(AgendaAnnouncementAdminResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<AgendaAnnouncementAdminResDto> pageResponseDto = PageResponseDto.of(
			agendaAnnouncementList.getTotalElements(), announceDtos);
		return ResponseEntity.ok(pageResponseDto);
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAgendaAnnouncement(
		@RequestBody @Valid AgendaAnnouncementAdminUpdateReqDto updateReqDto) {
		agendaAnnouncementAdminService.updateAgendaAnnouncement(updateReqDto);
		return ResponseEntity.noContent().build();
	}
}
