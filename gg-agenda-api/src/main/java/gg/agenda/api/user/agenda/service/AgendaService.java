package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAward;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	private final TicketService ticketService;

	@Transactional(readOnly = true)
	public Agenda findAgendaByAgendaKey(UUID agendaKey) {
		return agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Agenda> findCurrentAgendaList() {
		return agendaRepository.findAllByStatusIs(AgendaStatus.OPEN).stream()
			.sorted(Comparator.comparing(Agenda::getIsOfficial, Comparator.reverseOrder())
				.thenComparing(Agenda::getDeadline, Comparator.reverseOrder()))
			.collect(Collectors.toList());
	}

	@Transactional
	public Agenda addAgenda(AgendaCreateReqDto agendaCreateReqDto, UserDto user) {
		Agenda newAgenda = AgendaCreateReqDto.MapStruct.INSTANCE.toEntity(agendaCreateReqDto, user);
		return agendaRepository.save(newAgenda);
	}

	@Transactional(readOnly = true)
	public List<Agenda> findHistoryAgendaList(Pageable pageable) {
		return agendaRepository.findAllByStatusIs(pageable, AgendaStatus.FINISH).getContent();
	}

	@Transactional
	public void finishAgenda(Agenda agenda) {
		agenda.finishAgenda();
	}

	@Transactional
	public void awardAgenda(AgendaAwardsReqDto agendaAwardsReqDto, Agenda agenda) {
		List<AgendaTeam> teams = agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.CONFIRM);
		for (AgendaTeamAward agendaTeamAward : agendaAwardsReqDto.getAwards()) {
			AgendaTeam matchedTeam = teams.stream()
				.filter(team -> team.getName().equals(agendaTeamAward.getTeamName()))
				.findFirst()
				.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
			matchedTeam.acceptAward(agendaTeamAward.getAwardName(), agendaTeamAward.getAwardPriority());
		}
	}

	@Transactional
	public void confirmAgenda(Agenda agenda) {
		List<AgendaTeam> openTeams = agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
		for (AgendaTeam openTeam : openTeams) {
			List<AgendaProfile> participants = agendaTeamProfileRepository
				.findAllByAgendaTeamWithFetchProfile(openTeam).stream()
				.map(AgendaTeamProfile::getProfile)
				.collect(Collectors.toList());
			ticketService.refundTickets(participants, agenda.getAgendaKey());
			openTeam.cancelTeam();
		}
		agenda.confirmAgenda();
	}
}
