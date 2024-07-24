package gg.agenda.api.admin.agendateam.controller;

import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamResDto;
import gg.agenda.api.admin.agendateam.service.AgendaTeamAdminService;
import gg.data.agenda.AgendaTeam;
import gg.utils.dto.PageRequestDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/agenda/team")
@RequiredArgsConstructor
public class AgendaTeamAdminController {

	private final AgendaTeamAdminService agendaTeamAdminService;

	@GetMapping("/list")
	public ResponseEntity<List<AgendaTeamResDto>> getAgendaTeamList(@RequestParam("agenda_key") UUID agendaKey,
		@RequestBody @Valid PageRequestDto pageRequestDto) {
		int page = pageRequestDto.getPage();
		int size = pageRequestDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
		List<AgendaTeam> agendaTeamList = agendaTeamAdminService.getAgendaTeamList(agendaKey, pageable);
		List<AgendaTeamResDto> agendaTeamResDtoList = agendaTeamList.stream().map(AgendaTeamResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaTeamResDtoList);
	}
}
