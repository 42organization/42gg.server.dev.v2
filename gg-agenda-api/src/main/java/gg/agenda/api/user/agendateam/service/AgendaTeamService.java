package gg.agenda.api.user.agendateam.service;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
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
			ticketRepository.findByAgendaProfileAndIsApproveTrueAndIsUsedFalse(agendaProfile)
				.orElseThrow(() -> new ForbiddenException(TICKET_NOT_EXIST));
		}

		try {
			Location.valueOf(teamCreateReqDto.getTeamLocation().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(LOCATION_NOT_VALID);
		}

		agendaTeamRepository.findByAgendaAndTeamNameAndStatus(agenda, teamCreateReqDto.getTeamName(), OPEN, CONFIRM)
			.ifPresent(team -> {
				throw new DuplicationException(TEAM_NAME_EXIST);
			});

		AgendaTeam agendaTeam = TeamCreateReqDto.toEntity(teamCreateReqDto, agenda, user.getIntraId());
		agendaTeamRepository.save(agendaTeam);
		agendaRepository.save(agenda);

		return new TeamCreateResDto(agendaTeam.getTeamKey().toString());
	}
}
