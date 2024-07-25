package gg.repo.agenda;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;

public interface AgendaTeamProfileRepository extends JpaRepository<AgendaTeamProfile, Long> {
	@Query("SELECT atp FROM AgendaTeamProfile atp WHERE atp.agendaTeam.agenda = :agenda "
		+ "AND atp.profile = :agendaProfile AND atp.isExist = true")
	Optional<AgendaTeamProfile> findByAgendaProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);

	@Query("SELECT atp FROM AgendaTeamProfile atp WHERE atp.agendaTeam = :agendaTeam AND atp.isExist = true")
	List<AgendaTeamProfile> findByAgendaTeamAndIsExistTrue(AgendaTeam agendaTeam);

	Optional<AgendaTeamProfile> findByAgendaAndProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);

	List<AgendaTeamProfile> findAllByAgendaTeam(AgendaTeam agendaTeam);
}
