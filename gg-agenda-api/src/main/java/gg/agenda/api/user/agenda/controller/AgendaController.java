package gg.agenda.api.user.agenda.controller;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Optional;
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

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResDto;
import gg.agenda.api.user.agenda.service.AgendaService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.utils.dto.PageRequestDto;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.InvalidParameterException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	@GetMapping
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Agenda 상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "Agenda 조회 요청이 잘못됨"),
		@ApiResponse(responseCode = "404", description = "Agenda를 찾을 수 없음")
	})
	public ResponseEntity<AgendaResDto> agendaDetails(@RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		Optional<AgendaAnnouncement> announcement = agendaService.findAgendaWithLatestAnnouncement(agenda);
		String announcementTitle = announcement.map(AgendaAnnouncement::getTitle).orElse("");
		AgendaResDto agendaResDto = AgendaResDto.MapStruct.INSTANCE.toDto(agenda,  announcementTitle);
		return ResponseEntity.ok(agendaResDto);
	}

	@ApiResponse(responseCode = "200", description = "현재 진행중인 Agenda 목록 조회 성공")
	@GetMapping("/list")
	public ResponseEntity<List<AgendaSimpleResDto>> agendaListCurrent() {
		List<Agenda> agendaList = agendaService.findCurrentAgendaList();
		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendaList.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaSimpleResDtoList);
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Agenda 생성 성공"),
		@ApiResponse(responseCode = "400", description = "Agenda 생성 요청 파라미터가 잘못됨")
	})
	@PostMapping("/create")
	public ResponseEntity<AgendaKeyResDto> agendaAdd(@Login UserDto user,
		@RequestBody @Valid AgendaCreateDto agendaCreateDto) {
		UUID agendaKey = agendaService.addAgenda(agendaCreateDto, user).getAgendaKey();
		AgendaKeyResDto responseDto = AgendaKeyResDto.builder().agendaKey(agendaKey).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@ApiResponse(responseCode = "200", description = "지난 Agenda 목록 조회 성공")
	@GetMapping("/history")
	public ResponseEntity<List<AgendaSimpleResDto>> agendaListHistory(
		@RequestBody @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
		List<Agenda> agendas = agendaService.findHistoryAgendaList(pageable);
		List<AgendaSimpleResDto> agendaSimpleResDtoList = agendas.stream()
			.map(AgendaSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(agendaSimpleResDtoList);
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "203", description = "Agenda 참가 신청 성공"),
		@ApiResponse(responseCode = "400", description = "Agenda 참가 신청 요청이 잘못됨"),
		@ApiResponse(responseCode = "404", description = "Agenda를 찾을 수 없음"),
		@ApiResponse(responseCode = "409", description = "Agenda 참가 신청이 이미 완료됨")
	})
	@PatchMapping("/confirm")
	public ResponseEntity<Void> agendaConfirm(@RequestParam("agenda_key") UUID agendaKey, @Login UserDto user,
		@RequestBody(required = false) @Valid AgendaConfirmReqDto agendaConfirmReqDto) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		if (!user.getIntraId().equals(agenda.getHostIntraId())) {
			throw new ForbiddenException(CONFIRM_FORBIDDEN);
		}
		if (agenda.getIsRanking() && agendaConfirmReqDto == null) {
			throw new InvalidParameterException(AGENDA_INVALID_PARAM);
		}
		agendaService.confirmAgenda(agendaConfirmReqDto, agenda);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
