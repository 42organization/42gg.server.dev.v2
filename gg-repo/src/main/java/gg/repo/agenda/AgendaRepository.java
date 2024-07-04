package gg.repo.agenda;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
	Optional<Agenda> findByAgendaKey(UUID agendaKey);
}
