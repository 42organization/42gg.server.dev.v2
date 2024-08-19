package gg.agenda.api.admin.agendateam.controller;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamUpdateDto;
import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamDetailResDto;
import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamResDto;
import gg.agenda.api.admin.agendateam.service.AgendaTeamAdminService;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/admin/team")
@RequiredArgsConstructor
public class AgendaTeamAdminController {

	private final AgendaTeamAdminService agendaTeamAdminService;

	@GetMapping("/list")
	public ResponseEntity<PageResponseDto<AgendaTeamResDto>> agendaTeamList(@RequestParam("agenda_key") UUID agendaKey,
		@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		int page = pageRequestDto.getPage();
		int size = pageRequestDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaTeam> agendaTeamList = agendaTeamAdminService.getAgendaTeamList(agendaKey, pageable);

		List<AgendaTeamResDto> agendaTeamResDtos = agendaTeamList.stream()
			.map(AgendaTeamResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<AgendaTeamResDto> pageResponseDto = PageResponseDto.of(
			agendaTeamList.getTotalElements(), agendaTeamResDtos);
		return ResponseEntity.ok(pageResponseDto);
	}

	@GetMapping
	public ResponseEntity<AgendaTeamDetailResDto> agendaTeamDetail(@RequestParam("team_key") UUID agendaTeamKey) {
		AgendaTeam agendaTeam = agendaTeamAdminService.getAgendaTeamByTeamKey(agendaTeamKey);
		List<AgendaProfile> participants = agendaTeamAdminService.getAgendaProfileListByAgendaTeam(agendaTeam);
		AgendaTeamDetailResDto agendaTeamDetailResDto = AgendaTeamDetailResDto.MapStruct.INSTANCE
			.toDto(agendaTeam, participants);
		return ResponseEntity.ok(agendaTeamDetailResDto);
	}

	@PatchMapping
	public ResponseEntity<Void> agendaTeamUpdate(@RequestBody @Valid AgendaTeamUpdateDto agendaTeamUpdateDto) {
		agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
