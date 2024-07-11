package gg.agenda.api.user.agendateam.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.response.MyTeamSimpleResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/team")
public class AgendaTeamController {
	private final AgendaTeamService agendaTeamService;

	/**
	 * 내 팀 간단 정보 조회
	 * @param user 사용자 정보, agendaId 아젠다 아이디
	 * @return 내 팀 간단 정보
	 */
	@GetMapping("/my")
	public ResponseEntity<MyTeamSimpleResDto> myTeamSimpleDetails(@Parameter(hidden = true) @Login UserDto user,
		@RequestParam("agenda_key") UUID agendaKey) {
		MyTeamSimpleResDto myTeamSimpleResDto = agendaTeamService.detailsMyTeamSimple(user, agendaKey);
		return ResponseEntity.ok(myTeamSimpleResDto);
	}

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@PostMapping
	public ResponseEntity<TeamCreateResDto> agendaTeamAdd(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamCreateReqDto teamCreateReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamCreateResDto teamCreateResDto = agendaTeamService.addAgendaTeam(user, teamCreateReqDto, agendaKey);
		return ResponseEntity.status(HttpStatus.CREATED).body(teamCreateResDto);
	}
}
