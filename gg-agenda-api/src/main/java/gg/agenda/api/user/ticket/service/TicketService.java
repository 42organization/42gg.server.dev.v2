package gg.agenda.api.user.ticket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.repo.agenda.TicketRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;

	/**
	 * 티켓 환불
	 * @param changedProfiles 변경된 프로필 목록
	 * @param agendaKey 아젠다 키
	 * @Annotation 트랜잭션의 원자성을 보장하기 위해 부모 트랜잭션이 없을경우 예외를 발생시키는 Propagation.MANDATORY로 설정
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void refundTickets(List<AgendaProfile> changedProfiles, UUID agendaKey) {
		List<Ticket> tickets = new ArrayList<>();
		for (
			AgendaProfile profile : changedProfiles) {
			Ticket ticket = Ticket.refundTicket(profile, agendaKey);
			tickets.add(ticket);
		}
		ticketRepository.saveAll(tickets);
	}
}
