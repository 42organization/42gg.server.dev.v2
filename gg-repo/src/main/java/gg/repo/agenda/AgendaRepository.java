package gg.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
