package gg.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaTeam;

public interface AgendaTeamRepository extends JpaRepository<AgendaTeam, Long> {
}
