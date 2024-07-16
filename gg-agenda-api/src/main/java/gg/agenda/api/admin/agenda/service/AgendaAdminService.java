package gg.agenda.api.admin.agenda.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.data.agenda.Agenda;
import gg.repo.agenda.AgendaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaAdminService {

	private final AgendaRepository agendaRepository;

	public List<Agenda> getAgendaRequestList(Pageable pageable) {
		return agendaRepository.findAllByOrderByIdDesc(pageable).getContent();
	}
}
