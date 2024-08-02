package gg.agenda.api.user.agenda.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResDto;
import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaannouncement.service.AgendaAnnouncementService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.utils.dto.PageRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	private final AgendaAnnouncementService agendaAnnouncementService;

	@GetMapping
	public ResponseEntity<AgendaResDto> agendaDetails(@RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		String announcementTitle = agendaAnnouncementService
			.findLatestAnnounceTitleByAgendaOrDefault(agenda, "");
		AgendaResDto agendaResDto = AgendaResDto.MapStruct.INSTANCE.toDto(agenda, announcementTitle);
		return ResponseEntity.ok(agendaResDto);
	}

	@GetMapping("/list")
	public ResponseEntity<List<AgendaSimpleResDto>> agendaListCurrent() {
		List<Agenda> agendaList = agendaService.findCurrentAgendaList();
		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendaList.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaSimpleResDtoList);
	}

	@PostMapping("/request")
	public ResponseEntity<AgendaKeyResDto> agendaAdd(@Login @Parameter(hidden = true) UserDto user,
		@RequestBody @Valid AgendaCreateReqDto agendaCreateReqDto) {
		UUID agendaKey = agendaService.addAgenda(agendaCreateReqDto, user).getAgendaKey();
		AgendaKeyResDto responseDto = AgendaKeyResDto.builder().agendaKey(agendaKey).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@GetMapping("/history")
	public ResponseEntity<List<AgendaSimpleResDto>> agendaListHistory(@RequestParam @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
		List<Agenda> agendas = agendaService.findHistoryAgendaList(pageable);
		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendas.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaSimpleResDtoList);
	}

	@PatchMapping("/finish")
	public ResponseEntity<Void> agendaEndWithAwards(@RequestParam("agenda_key") UUID agendaKey,
		@RequestBody @Valid AgendaAwardsReqDto agendaAwardsReqDto, @Login @Parameter(hidden = true) UserDto user) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		if (agenda.getIsRanking()) {
			agendaService.awardAgenda(agendaAwardsReqDto, agenda);
		}
		agendaService.finishAgenda(agenda);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/confirm")
	public ResponseEntity<Void> agendaConfirm(@RequestParam("agenda_key") UUID agendaKey,
		@Login @Parameter(hidden = true) UserDto user) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		agendaService.confirmAgendaAndRefundTicketForOpenTeam(agenda);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
