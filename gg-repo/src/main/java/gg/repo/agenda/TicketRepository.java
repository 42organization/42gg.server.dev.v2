package gg.repo.agenda;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
	Optional<Ticket> findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
		AgendaProfile agendaProfile);

	Optional<Ticket> findByAgendaProfileAndIsApprovedFalse(AgendaProfile agendaProfile);

	Optional<Ticket> findByAgendaProfileId(Long agendaProfileId);

	Page<Ticket> findByAgendaProfileId(Long agendaProfileId, Pageable pageable);

	List<Ticket> findByAgendaProfileAndIsUsedFalseAndIsApprovedTrue(AgendaProfile agendaProfile);
}
