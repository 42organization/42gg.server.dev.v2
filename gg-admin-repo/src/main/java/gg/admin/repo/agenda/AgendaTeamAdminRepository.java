package gg.admin.repo.agenda;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;

@Repository
public interface AgendaTeamAdminRepository extends JpaRepository<AgendaTeam, Long> {

	@Query("SELECT at FROM AgendaTeam at WHERE at.agenda = :agenda")
	List<AgendaTeam> findAllByAgenda(Agenda agenda);

	@Query("SELECT at FROM AgendaTeam at WHERE at.agenda = :agenda")
	Page<AgendaTeam> findAllByAgenda(Agenda agenda, Pageable pageable);
}
