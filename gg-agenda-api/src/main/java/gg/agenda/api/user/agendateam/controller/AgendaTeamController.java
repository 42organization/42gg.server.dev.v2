package gg.agenda.api.user.agendateam.controller;

import static gg.data.agenda.type.AgendaTeamStatus.*;

import java.util.List;
import java.util.Optional;
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

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamUpdateReqDto;
import gg.agenda.api.user.agendateam.controller.response.ConfirmTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.MyTeamSimpleResDto;
import gg.agenda.api.user.agendateam.controller.response.OpenTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamKeyResDto;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.agenda.api.utils.AgendaSlackService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.Coalition;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/team")
public class AgendaTeamController {
	private final AgendaService agendaService;
	private final AgendaTeamService agendaTeamService;
	private final AgendaSlackService agendaSlackService;

	/**
	 * 내 팀 간단 정보 조회
	 * @param user 사용자 정보, agendaId 아젠다 아이디
	 * @return 내 팀 간단 정보
	 */
	@GetMapping("/my")
	public ResponseEntity<Optional<MyTeamSimpleResDto>> myTeamSimpleDetails(
		@Parameter(hidden = true) @Login UserDto user, @RequestParam("agenda_key") UUID agendaKey) {
		Optional<MyTeamSimpleResDto> myTeamSimpleResDto = agendaTeamService.detailsMyTeamSimple(user, agendaKey);
		if (myTeamSimpleResDto.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(myTeamSimpleResDto);
	}

	/*
	 * 아젠다 팀 상세 정보 조회
	 * @param user 사용자 정보, teamDetailsReqDto 팀 상세 정보 요청 정보, agendaId 아젠다 아이디
	 * @return 팀 상세 정보
	 */
	@GetMapping
	public ResponseEntity<TeamDetailsResDto> agendaTeamDetails(@Parameter(hidden = true) @Login UserDto user,
		@ModelAttribute @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamDetailsResDto teamDetailsResDto = agendaTeamService.detailsAgendaTeam(user, agendaKey, teamKeyReqDto);
		return ResponseEntity.ok(teamDetailsResDto);
	}

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@PostMapping
	public ResponseEntity<TeamKeyResDto> agendaTeamAdd(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamCreateReqDto teamCreateReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamKeyResDto teamKeyReqDto = agendaTeamService.addAgendaTeam(user, teamCreateReqDto, agendaKey);
		return ResponseEntity.status(HttpStatus.CREATED).body(teamKeyReqDto);
	}

	/**
	 * 아젠다 팀 확정하기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping("/confirm")
	public ResponseEntity<Void> confirmTeam(@Parameter(hidden = true) @Login UserDto user,
		@ModelAttribute @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		AgendaTeam agendaTeam = agendaTeamService.confirmTeam(user, agenda, teamKeyReqDto.getTeamKey());
		agendaSlackService.slackConfirmAgendaTeam(agenda, agendaTeam);
		return ResponseEntity.ok().build();
	}

	/**
	 * 아젠다 팀장 나가기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping("/cancel")
	public ResponseEntity<Void> leaveAgendaTeam(@Parameter(hidden = true) @Login UserDto user,
		@ModelAttribute @Valid TeamKeyReqDto teamKeyReqDto) {
		AgendaTeam agendaTeam = agendaTeamService.getAgendaTeam(teamKeyReqDto.getTeamKey());
		agendaTeam.getAgenda().agendaStatusMustBeOpen();
		agendaTeam.agendaTeamStatusMustBeOpenAndConfirm();
		agendaTeamService.leaveTeamAll(agendaTeam);
		agendaSlackService.slackCancelAgendaTeam(agendaTeam.getAgenda(), agendaTeam);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 아젠다 팀원 나가기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping("/drop")
	public ResponseEntity<Void> dropAgendaTeamMate(@Parameter(hidden = true) @Login UserDto user,
		@ModelAttribute @Valid TeamKeyReqDto teamKeyReqDto) {
		AgendaTeam agendaTeam = agendaTeamService.getAgendaTeam(teamKeyReqDto.getTeamKey());
		agendaTeam.getAgenda().agendaStatusMustBeOpen();
		agendaTeam.agendaTeamStatusMustBeOpen();
		agendaTeamService.leaveTeamMate(agendaTeam, user);
		agendaSlackService.slackLeaveTeamMate(agendaTeam.getAgenda(), agendaTeam, user.getIntraId());
		return ResponseEntity.noContent().build();
	}

	/**
	 * 아젠다 팀 공개 모집인 팀 목록 조회
	 * @param pageRequest 페이지네이션 요청 정보, agendaId 아젠다 아이디
	 */
	@GetMapping("/open/list")
	public ResponseEntity<PageResponseDto<OpenTeamResDto>> openTeamList(
		@ModelAttribute @Valid PageRequestDto pageRequest, @RequestParam("agenda_key") UUID agendaKey) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaTeam> openTeams = agendaTeamService.findAgendaTeamWithStatus(agendaKey, OPEN, pageable);

		List<OpenTeamResDto> openTeamResDtoList = openTeams.stream()
			.map(agendaTeam -> {
				List<Coalition> coalitions = agendaTeamService.getCoalitionsFromAgendaTeam(agendaTeam);
				return new OpenTeamResDto(agendaTeam, coalitions);
			})
			.collect(Collectors.toList());

		PageResponseDto<OpenTeamResDto> pageResponseDto = PageResponseDto.of(
			openTeams.getTotalElements(), openTeamResDtoList);
		return ResponseEntity.ok(pageResponseDto);
	}

	/**
	 * 아젠다 팀 확정된 팀 목록 조회
	 * @param pageRequest 페이지네이션 요청 정보, agendaId 아젠다 아이디
	 */
	@GetMapping("/confirm/list")
	public ResponseEntity<PageResponseDto<ConfirmTeamResDto>> confirmTeamList(
		@ModelAttribute @Valid PageRequestDto pageRequest, @RequestParam("agenda_key") UUID agendaKey) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaTeam> confirmTeams = agendaTeamService.findAgendaTeamWithStatus(agendaKey, CONFIRM, pageable);

		List<ConfirmTeamResDto> confirmTeamResDtoList = confirmTeams.stream()
			.map(agendaTeam -> {
				List<Coalition> coalitions = agendaTeamService.getCoalitionsFromAgendaTeam(agendaTeam);
				return new ConfirmTeamResDto(agendaTeam, coalitions);
			})
			.collect(Collectors.toList());

		PageResponseDto<ConfirmTeamResDto> pageResponseDto = PageResponseDto.of(
			confirmTeams.getTotalElements(), confirmTeamResDtoList);
		return ResponseEntity.ok(pageResponseDto);
	}

	/**
	 * 아젠다 팀 참여하기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PostMapping("/join")
	public ResponseEntity<Void> attendTeamModify(@Parameter(hidden = true) @Login UserDto user,
		@ModelAttribute @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		Agenda agenda = agendaService.findAgendaByAgendaKey(agendaKey);
		AgendaTeam agendaTeam = agendaTeamService.getAgendaTeam(teamKeyReqDto.getTeamKey());
		agendaTeamService.modifyAttendTeam(user, agendaTeam, agenda);
		agendaSlackService.slackAttendTeamMate(agenda, agendaTeam, user.getIntraId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 아젠다 팀 수정하기
	 * @param user 사용자 정보, teamUpdateReqDto 팀 update 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping
	public ResponseEntity<Void> agendaTeamModify(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamUpdateReqDto teamUpdateReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		agendaTeamService.modifyAgendaTeam(user, teamUpdateReqDto, agendaKey);
		return ResponseEntity.noContent().build();
	}
}
