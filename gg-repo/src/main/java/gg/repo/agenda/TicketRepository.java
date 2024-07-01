package gg.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
