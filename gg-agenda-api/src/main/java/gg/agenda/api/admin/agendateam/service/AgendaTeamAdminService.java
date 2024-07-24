package gg.agenda.api.admin.agendateam.service;

import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.data.agenda.AgendaTeam;
import gg.utils.dto.PageRequestDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgendaTeamAdminService {

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	public List<AgendaTeam> getAgendaTeamList(UUID agendaKey, Pageable pageable) {
		return null;
	}
}
