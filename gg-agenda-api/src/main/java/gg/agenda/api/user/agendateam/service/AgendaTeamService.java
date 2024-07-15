package gg.agenda.api.user.agendateam.service;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.response.MyTeamSimpleResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaTeamService {
	private final AgendaRepository agendaRepository;
	private final TicketRepository ticketRepository;
	private final AgendaTeamRepository agendaTeamRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	/**
	 * 내 팀 간단 정보 조회
	 * @param user 사용자 정보, agendaId 아젠다 아이디
	 * @return 내 팀 간단 정보
	 */
	public Optional<MyTeamSimpleResDto> detailsMyTeamSimple(UserDto user, UUID agendaKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Optional<AgendaTeam> agendaTeam = agendaTeamProfileRepository.findByAgendaAndIsExistTrue(agenda, agendaProfile)
			.map(AgendaTeamProfile::getAgendaTeam);
		if (agendaTeam.isEmpty()) {
			return Optional.empty();
		}

		List<AgendaTeamProfile> agendaTeamProfileList = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam.get());

		List<Coalition> coalitions = agendaTeamProfileList.stream()
			.map(AgendaTeamProfile::getProfile)
			.map(AgendaProfile::getCoalition)
			.collect(Collectors.toList());

		return Optional.of(new MyTeamSimpleResDto(agendaTeam.get(), coalitions));
	}

	/**
	 * 아젠다 팀 상세 정보 조회
	 * @param user 사용자 정보, teamCreateReqDto 팀 키, agendaKey 아젠다 키
	 * @return 만들어진 팀 상세 정보
	 */
	@Transactional(readOnly = true)
	public TeamDetailsResDto detailsAgendaTeam(UserDto user, UUID agendaKey, TeamKeyReqDto teamKeyReqDto) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamKeyReqDto.getTeamKey(), OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		List<AgendaTeamProfile> agendaTeamProfileList = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam);

		if (agendaTeam.getStatus().equals(CONFIRM)) {  // 팀이 확정 상태인 경우에
			if (agendaTeamProfileList.stream() // 팀에 속한 유저가 아닌 경우
				.noneMatch(profile -> profile.getProfile().getUserId().equals(user.getId()))) {
				throw new ForbiddenException(TEAM_FORBIDDEN); // 조회 불가
			}
		}
		return new TeamDetailsResDto(agendaTeam, agendaTeamProfileList);
	}

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@Transactional
	public TeamCreateResDto addAgendaTeam(UserDto user, TeamCreateReqDto teamCreateReqDto, UUID agendaKey) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		agenda.addTeam(Location.valueOf(teamCreateReqDto.getTeamLocation()), LocalDateTime.now());

		if (agenda.getHostIntraId().equals(user.getIntraId())) {
			throw new ForbiddenException(HOST_FORBIDDEN);
		}

		if (agenda.getLocation() != Location.MIX && agenda.getLocation() != agendaProfile.getLocation()) {
			throw new BusinessException(LOCATION_NOT_VALID);
		}

		agendaTeamProfileRepository.findByAgendaAndIsExistTrue(agenda, agendaProfile).ifPresent(teamProfile -> {
			throw new DuplicationException(TEAM_FORBIDDEN);
		});

		if (agenda.getIsOfficial()) {
			Ticket ticket = ticketRepository.findByAgendaProfileAndIsApproveTrueAndIsUsedFalse(agendaProfile)
				.orElseThrow(() -> new ForbiddenException(TICKET_NOT_EXIST));
			ticket.useTicket();
		}

		agendaTeamRepository.findByAgendaAndTeamNameAndStatus(agenda, teamCreateReqDto.getTeamName(), OPEN, CONFIRM)
			.ifPresent(team -> {
				throw new DuplicationException(TEAM_NAME_EXIST);
			});

		AgendaTeam agendaTeam = TeamCreateReqDto.toEntity(teamCreateReqDto, agenda, user.getIntraId());
		AgendaTeamProfile agendaTeamProfile = new AgendaTeamProfile(agendaTeam, agendaProfile);
		agendaRepository.save(agenda);
		agendaTeamRepository.save(agendaTeam);
		agendaTeamProfileRepository.save(agendaTeamProfile);
		return new TeamCreateResDto(agendaTeam.getTeamKey().toString());
	}

	@Transactional
	public void confirmTeam(UserDto user, UUID agendaKey, TeamKeyReqDto teamKeyReqDto) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamKeyReqDto.getTeamKey(), OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		if (!agendaTeam.getLeaderIntraId().equals(user.getIntraId())) {
			throw new ForbiddenException(TEAM_LEADER_FORBIDDEN);
		}
		if (agendaTeam.getMateCount() < agenda.getMinPeople()) {
			throw new BusinessException(NOT_ENOUGH_TEAM_MEMBER);
		}
		agenda.checkAgenda(agendaTeam.getLocation(), LocalDateTime.now());
		agendaTeam.confirm();
		agendaTeamRepository.save(agendaTeam);
	}
}
