package gg.admin.repo.agenda;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;

@Repository
public interface TicketAdminRepository extends JpaRepository<Ticket, Long> {
	Optional<Ticket> findByAgendaProfile(AgendaProfile agendaProfile);

	Page<Ticket> findByAgendaProfile(AgendaProfile agendaProfile, Pageable pageable);
}
