package gg.agenda.api.admin.agenda.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.agenda.controller.response.AgendaAdminResDto;
import gg.agenda.api.admin.agenda.service.AgendaAdminService;
import gg.utils.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/agenda")
@RequiredArgsConstructor
public class AgendaAdminController {

	private final AgendaAdminService agendaAdminService;

	@GetMapping("/request/list")
	public ResponseEntity<List<AgendaAdminResDto>> getAgendaRequestList(
		@RequestBody @Valid PageRequestDto pageDto) {
		int page = pageDto.getPage();
		int size = pageDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
		List<AgendaAdminResDto> agendaDtos = agendaAdminService.getAgendaRequestList(pageable).stream()
			.map(AgendaAdminResDto.MapStruct.INSTANCE::toAgendaAdminResDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaDtos);
	}
}
