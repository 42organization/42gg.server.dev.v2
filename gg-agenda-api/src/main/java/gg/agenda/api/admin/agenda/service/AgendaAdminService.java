package gg.agenda.api.admin.agenda.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.data.agenda.Agenda;
import gg.repo.agenda.AgendaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	public List<Agenda> getAgendaRequestList(Pageable pageable) {
		return agendaAdminRepository.findAll(pageable).getContent();
	}
}
