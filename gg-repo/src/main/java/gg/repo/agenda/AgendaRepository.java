package gg.repo.agenda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
	@Query("SELECT a FROM Agenda a WHERE a.agendaKey = :agendaKey")
	Optional<Agenda> findByAgendaKey(UUID agendaKey);

	@Query("SELECT a FROM Agenda a WHERE a.status = :status")
	List<Agenda> findAllByStatusIs(AgendaStatus status);

	Page<Agenda> findAllByStatusIs(AgendaStatus status, Pageable pageable);

	Optional<Agenda> findAgendaByAgendaKey(UUID usedTo);

	@Query("SELECT a FROM Agenda a WHERE a.hostIntraId = :intraId AND (a.status = :status1 OR a.status = :status2)")
	Page<Agenda> findAllByHostIntraIdAndStatus(
		String intraId, AgendaStatus status1, AgendaStatus status2, Pageable pageable);
}
