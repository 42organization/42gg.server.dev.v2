package gg.repo.agenda;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

	@Query("SELECT a FROM Agenda a WHERE key = :key")
	Optional<Agenda> findAgendaByKey(UUID key);
}
