package gg.agenda.api.admin.agendateam.service;

import static gg.utils.exception.ErrorCode.*;

import gg.admin.repo.agenda.AgendaTeamProfileAdminRepository;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeamProfile;
import gg.repo.agenda.AgendaTeamProfileRepository;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgendaTeamAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	private final AgendaTeamProfileAdminRepository agendaTeamProfileAdminRepository;

	@Transactional(readOnly = true)
	public List<AgendaTeam> getAgendaTeamList(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		return agendaTeamAdminRepository.findAllByAgenda(agenda, pageable).getContent();
	}

	@Transactional(readOnly = true)
	public AgendaTeam getAgendaTeamByTeamKey(UUID teamKey) {
		return agendaTeamAdminRepository.findByTeamKey(teamKey)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<AgendaProfile> getAgendaProfileListByAgendaTeam(AgendaTeam agendaTeam) {
		return agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam).stream()
			.map(AgendaTeamProfile::getProfile).collect(Collectors.toList());
	}
}
