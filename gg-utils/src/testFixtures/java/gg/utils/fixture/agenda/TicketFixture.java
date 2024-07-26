package gg.utils.fixture.agenda;

import java.time.LocalDateTime;

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

	public void createNotApporveTicket(AgendaProfile seoulUserAgendaProfile) {
		Ticket ticket = Ticket.builder()
			.agendaProfile(seoulUserAgendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(false)
			.approvedAt(null)
			.isUsed(false)
			.usedAt(null)
			.build();
		ticketRepository.save(ticket);
	}
}
