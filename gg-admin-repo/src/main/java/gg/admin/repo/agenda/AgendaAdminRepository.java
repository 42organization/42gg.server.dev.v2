package gg.admin.repo.agenda;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Agenda;

@Repository
public interface AgendaAdminRepository extends JpaRepository<Agenda, Long> {

	@Query("SELECT a FROM Agenda a WHERE a.agendaKey = :agendaKey")
	Optional<Agenda> findByAgendaKey(UUID agendaKey);

	boolean existsByAgendaKey(UUID issuedFromKey);
}
