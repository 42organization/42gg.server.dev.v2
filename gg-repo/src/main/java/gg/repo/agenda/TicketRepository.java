package gg.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
	Optional<Ticket> findByAgendaProfileAndIsApproveTrueAndIsUsedFalse(AgendaProfile agendaProfile);
}
