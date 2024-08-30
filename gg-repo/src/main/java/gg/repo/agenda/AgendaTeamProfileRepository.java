package gg.repo.agenda;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;

public interface AgendaTeamProfileRepository extends JpaRepository<AgendaTeamProfile, Long> {
	@Query("SELECT atp FROM AgendaTeamProfile atp "
		+ "WHERE atp.agendaTeam.agenda = :agenda AND atp.profile = :agendaProfile AND atp.isExist = true")
	Optional<AgendaTeamProfile> findByAgendaAndAgendaProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);

	@Query("SELECT atp FROM AgendaTeamProfile atp "
		+ "WHERE atp.agendaTeam = :agendaTeam AND atp.isExist = true")
	List<AgendaTeamProfile> findByAgendaTeamAndIsExistTrue(AgendaTeam agendaTeam);

	@Query("SELECT atp FROM AgendaTeamProfile atp "
		+ "WHERE atp.agenda = :agenda AND atp.profile = :agendaProfile AND atp.isExist = true")
	Optional<AgendaTeamProfile> findByAgendaAndProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);

	@Query("SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.profile "
		+ "WHERE atp.agendaTeam = :agendaTeam AND atp.isExist = true")
	List<AgendaTeamProfile> findAllByAgendaTeamAndIsExistTrue(AgendaTeam agendaTeam);

	@Query("SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.agenda "
		+ "WHERE atp.profile = :agendaProfile AND atp.isExist = true")
	List<AgendaTeamProfile> findByProfileAndIsExistTrue(@Param("agendaProfile") AgendaProfile agendaProfile);

	@Query(
		value = "SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.agendaTeam at "
		+ "WHERE atp.profile = :agendaProfile AND atp.isExist = true AND atp.agenda.status = :status",
		countQuery = "SELECT count(atp) FROM AgendaTeamProfile atp "
		+ "WHERE atp.profile = :agendaProfile AND atp.isExist = true AND atp.agenda.status = :status")
	Page<AgendaTeamProfile> findByProfileAndIsExistTrueAndAgendaStatus(
		AgendaProfile agendaProfile, AgendaStatus status, Pageable pageable);

	@Query("SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.profile "
		+ "WHERE atp.agenda = :agenda AND atp.isExist = true")
	List<AgendaTeamProfile> findAllByAgendaAndIsExistTrue(Agenda agenda);

	@Query("SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.profile "
		+ "WHERE atp.agendaTeam IN :agendaTeams AND atp.isExist = true")
	List<AgendaTeamProfile> findByAgendaTeamInAndIsExistTrue(List<AgendaTeam> agendaTeams);
}
