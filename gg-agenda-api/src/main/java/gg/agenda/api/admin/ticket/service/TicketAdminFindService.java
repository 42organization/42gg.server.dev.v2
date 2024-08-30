package gg.agenda.api.admin.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.TicketAdminRepository;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAdminFindService {
	private final AgendaProfileRepository agendaProfileRepository;
	private final TicketAdminRepository ticketAdminRepository;
	private final AgendaAdminRepository agendaAdminRepository;

	@Transactional(readOnly = true)
	public Page<Ticket> findTicket(String intraId, Pageable pageable) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		return ticketAdminRepository.findByAgendaProfile(agendaProfile, pageable);
	}
}
