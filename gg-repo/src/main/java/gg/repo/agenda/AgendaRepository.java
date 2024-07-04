package gg.repo.agenda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

	@Query("SELECT a FROM Agenda a WHERE a.agendaKey = :agendaKey")
	Optional<Agenda> findAgendaByKey(UUID agendaKey);

	@Query("SELECT a FROM Agenda a WHERE a.status = :status")
	List<Agenda> findAllByStatusIs(AgendaStatus status);
}
