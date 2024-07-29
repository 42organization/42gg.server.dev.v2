package gg.agenda.api.admin.agendateam.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaTeamAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	public List<AgendaTeam> getAgendaTeamList(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		return agendaTeamAdminRepository.findAllByAgenda(agenda, pageable).getContent();
	}
}
