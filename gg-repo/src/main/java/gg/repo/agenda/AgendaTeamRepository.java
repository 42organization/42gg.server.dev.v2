package gg.repo.agenda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaTeamStatus;

public interface AgendaTeamRepository extends JpaRepository<AgendaTeam, Long> {
	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND a.name = :teamName AND"
		+ " (a.status = :status1 OR a.status = :status2)")
	Optional<AgendaTeam> findByAgendaAndTeamNameAndStatus(Agenda agenda, String teamName, AgendaTeamStatus status1,
		AgendaTeamStatus status2);

	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND a.teamKey = :teamKey AND"
		+ " (a.status = :status1 OR a.status = :status2)")
	Optional<AgendaTeam> findByAgendaAndTeamKeyAndStatus(Agenda agenda, UUID teamKey, AgendaTeamStatus status1,
		AgendaTeamStatus status2);

	@Query("SELECT a FROM AgendaTeam a JOIN FETCH a.agenda WHERE a.teamKey = :teamKey")
	Optional<AgendaTeam> findByTeamKey(UUID teamKey);

	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND a.status = :status")
	List<AgendaTeam> findAllByAgendaAndStatus(Agenda agenda, AgendaTeamStatus status);

	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND (a.status = :status1 OR a.status = :status2)")
	List<AgendaTeam> findAllByAgendaAndStatus(Agenda agenda, AgendaTeamStatus status1, AgendaTeamStatus status2);

	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND a.status = :status AND a.isPrivate = false")
	Page<AgendaTeam> findByAgendaAndStatusAndIsPrivateFalse(Agenda agenda, AgendaTeamStatus status, Pageable pageable);

	@Query("SELECT a FROM AgendaTeam a WHERE a.agenda = :agenda AND a.status = :status")
	Page<AgendaTeam> findByAgendaAndStatus(Agenda agenda, AgendaTeamStatus status, Pageable pageable);
}
