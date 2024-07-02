package gg.agenda.api.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.agenda.Agenda;
import gg.repo.agenda.AgendaRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	@Transactional(readOnly = true)
	public Agenda findAgenda(UUID agendaKey) {
		return agendaRepository.findAgendaByKey(agendaKey).orElseThrow(
			() -> new NotExistException("Agenda not found. agendaKey: " + agendaKey)
		);
	}
}
