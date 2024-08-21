package gg.agenda.api.user.agendaprofile.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendaprofile.controller.response.HostedAgendaResDto;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/host")
public class AgendaHostController {

	private final AgendaProfileFindService agendaProfileFindService;

	@GetMapping("/history/list")
	public ResponseEntity<PageResponseDto<HostedAgendaResDto>> hostedAgendaList(
		@Login @Parameter(hidden = true) UserDto user,
		@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		int page = pageRequestDto.getPage();
		int size = pageRequestDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<Agenda> hostedAgendas = agendaProfileFindService.findHostedAgenda(user.getIntraId(), pageable);

		List<HostedAgendaResDto> agendaResDtos = hostedAgendas.stream()
			.map(HostedAgendaResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<HostedAgendaResDto> pageResponseDto = PageResponseDto.of(
			hostedAgendas.getTotalElements(), agendaResDtos);
		return ResponseEntity.ok(pageResponseDto);
	}

	@GetMapping("/current/list")
	public ResponseEntity<PageResponseDto<HostedAgendaResDto>> hostingAgendaList(
		@Login @Parameter(hidden = true) UserDto user,
		@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		int page = pageRequestDto.getPage();
		int size = pageRequestDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<Agenda> hostedAgendas = agendaProfileFindService.findHostingAgenda(user.getIntraId(), pageable);

		List<HostedAgendaResDto> agendaResDtos = hostedAgendas.stream()
			.map(HostedAgendaResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<HostedAgendaResDto> pageResponseDto = PageResponseDto.of(
			hostedAgendas.getTotalElements(), agendaResDtos);
		return ResponseEntity.ok(pageResponseDto);
	}
}
