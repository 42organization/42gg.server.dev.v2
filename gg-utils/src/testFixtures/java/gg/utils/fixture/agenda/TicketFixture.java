package gg.utils.fixture.agenda;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.repo.agenda.TicketRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketFixture {
	private final TicketRepository ticketRepository;

	public Ticket createTicket(AgendaProfile agendaProfile) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(true)
			.approvedAt(LocalDateTime.now().minusDays(1))
			.isUsed(false)
			.usedAt(null)
			.build();
		return ticketRepository.save(ticket);
	}

	public Ticket createNotApporveTicket(AgendaProfile seoulUserAgendaProfile) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(seoulUserAgendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(false)
			.approvedAt(null)
			.isUsed(false)
			.usedAt(null)
			.build();
		return ticketRepository.save(ticket);
	}

	public Ticket createTicket(AgendaProfile agendaProfile, boolean isApproved, boolean isUsed, UUID issuedFrom,
		UUID usedTo) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(issuedFrom)
			.usedTo(usedTo)
			.isApproved(isApproved)
			.approvedAt(LocalDateTime.now().minusDays(1))
			.isUsed(isUsed)
			.usedAt(null)
			.build();
		ticketRepository.save(ticket);
		return ticket;
	}
}
