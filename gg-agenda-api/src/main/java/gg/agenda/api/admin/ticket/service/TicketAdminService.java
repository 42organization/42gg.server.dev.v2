package gg.agenda.api.admin.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.admin.repo.agenda.TicketAdminRepository;
import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAdminService {

	private final TicketAdminRepository ticketRepository;
	private final AgendaProfileAdminRepository agendaProfileRepository;
	private final AgendaAdminRepository agendaAdminRepository;

	/**
	 * 티켓 설정 추가
	 * @param intraId 사용자 정보
	 */
	@Transactional
	public Long addTicket(String intraId, TicketAddAdminReqDto ticketAddAdminReqDto) {
		AgendaProfile profile = agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		UUID issuedFromKey = ticketAddAdminReqDto.getIssuedFromKey();

		if (issuedFromKey != null) {
			if (!agendaAdminRepository.existsByAgendaKey(issuedFromKey)) {
				throw new NotExistException(AGENDA_NOT_FOUND);
			}
			Ticket ticket = Ticket.createUsedAdminTicket(profile, issuedFromKey);
			return ticketRepository.save(ticket).getId();
		}

		Ticket ticket = Ticket.createAdminTicket(profile);
		return ticketRepository.save(ticket).getId();
	}
}
