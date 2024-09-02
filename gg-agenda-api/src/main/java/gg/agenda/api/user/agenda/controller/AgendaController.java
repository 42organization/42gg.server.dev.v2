package gg.agenda.api.user.agenda.controller;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResDto;
import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaannouncement.service.AgendaAnnouncementService;
import gg.agenda.api.utils.AgendaSlackService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import gg.utils.exception.custom.InvalidParameterException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

	private final AgendaService agendaService;
	private final AgendaSlackService agendaSlackService;
	private final AgendaAnnouncementService agendaAnnouncementService;

	@GetMapping
	public ResponseEntity<AgendaResDto> agendaDetails(@RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		String announcementTitle = agendaAnnouncementService
			.findLatestAnnounceTitleByAgendaOrDefault(agenda, "");
		AgendaResDto agendaResDto = AgendaResDto.MapStruct.INSTANCE.toDto(agenda, announcementTitle);
		return ResponseEntity.ok(agendaResDto);
	}

	@GetMapping("/confirm")
	public ResponseEntity<List<AgendaSimpleResDto>> agendaListConfirm() {
		List<Agenda> agendaList = agendaService.findCurrentAgendaList();
		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendaList.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaSimpleResDtoList);
	}

	@PostMapping("/request")
	public ResponseEntity<AgendaKeyResDto> agendaAdd(@Login @Parameter(hidden = true) UserDto user,
		@ModelAttribute @Valid AgendaCreateReqDto agendaCreateReqDto,
		@RequestParam(required = false) MultipartFile agendaPoster) {
		if (Objects.nonNull(agendaPoster) && agendaPoster.getSize() > 1024 * 1024) {    // 1MB
			throw new InvalidParameterException(AGENDA_POSTER_SIZE_TOO_LARGE);
		}
		UUID agendaKey = agendaService.addAgenda(agendaCreateReqDto, agendaPoster, user).getAgendaKey();
		AgendaKeyResDto responseDto = AgendaKeyResDto.builder().agendaKey(agendaKey).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@GetMapping("/history")
	public ResponseEntity<PageResponseDto<AgendaSimpleResDto>> agendaListHistory(
		@ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());

		Page<Agenda> agendas = agendaService.findHistoryAgendaList(pageable);

		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendas.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		PageResponseDto<AgendaSimpleResDto> pageResponseDto = PageResponseDto.of(
			agendas.getTotalElements(), agendaSimpleResDtoList);
		return ResponseEntity.ok(pageResponseDto);
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
		agendaSlackService.slackFinishAgenda(agenda);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/confirm")
	public ResponseEntity<Void> agendaConfirm(@RequestParam("agenda_key") UUID agendaKey,
		@Login @Parameter(hidden = true) UserDto user) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		List<AgendaTeam> failTeam = agendaService.confirmAgendaAndRefundTicketForOpenTeam(agenda);
		agendaSlackService.slackConfirmAgenda(agenda);
		agendaSlackService.slackCancelByAgendaConfirm(agenda, failTeam);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/cancel")
	public ResponseEntity<Void> agendaCancel(@RequestParam("agenda_key") UUID agendaKey,
		@Login @Parameter(hidden = true) UserDto user) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		agenda.mustModifiedByHost(user.getIntraId());
		agendaService.cancelAgenda(agenda);
		agendaSlackService.slackCancelAgenda(agenda);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
