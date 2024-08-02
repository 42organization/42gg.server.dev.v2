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

	@Query("SELECT atp FROM AgendaTeamProfile atp WHERE atp.agenda = :agenda AND atp.profile = :agendaProfile "
		+ "AND atp.isExist = true")
	Optional<AgendaTeamProfile> findByAgendaAndProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);

	/**
	 * 해당 메서드는 N+1 문제가 발생할 수 있습니다.
	 */
	List<AgendaTeamProfile> findAllByAgendaTeam(AgendaTeam agendaTeam);

	List<AgendaTeamProfile> findByProfile(AgendaProfile agendaProfile);
}
