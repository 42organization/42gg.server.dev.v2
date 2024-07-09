package gg.agenda.api.user.agendateam.service;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamDetailsReqDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
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
	 * 아젠다 팀 상세 정보 조회
	 * @param user 사용자 정보, teamCreateReqDto 팀 키, agendaKey 아젠다 키
	 * @return 만들어진 팀 상세 정보
	 */
	public TeamDetailsResDto detailsAgendaTeam(UserDto user, UUID agendaKey, TeamDetailsReqDto teamDetailsReqDto) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository.findByAgendaAndTeamKeyAndStatus(agenda,
			teamDetailsReqDto.getTeamKey(), OPEN, CONFIRM).orElseThrow(() -> new NotExistException(TEAM_NOT_FOUND));

		List<AgendaTeamProfile> agendaTeamProfileList = agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(
			agendaTeam);

		if (agendaTeam.getIsPrivate() || agendaTeam.getStatus().equals(CONFIRM)) {
			if (agendaTeamProfileList.stream()
				.noneMatch(profile -> profile.getProfile().getUserId().equals(user.getId()))) {
				throw new ForbiddenException(TEAM_FORBIDDEN);
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
			.orElseThrow(() -> new NotExistException("해당 유저의 프로필이 존재하지 않습니다."));

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
}
