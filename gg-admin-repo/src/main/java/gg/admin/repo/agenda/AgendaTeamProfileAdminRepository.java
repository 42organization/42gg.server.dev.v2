package gg.admin.repo.agenda;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;

@Repository
public interface AgendaTeamProfileAdminRepository extends JpaRepository<AgendaTeamProfile, Long> {

	List<AgendaTeamProfile> findAllByAgendaTeamAndIsExistIsTrue(AgendaTeam agendaTeam);

	@Query("SELECT atp FROM AgendaTeamProfile atp WHERE atp.agenda = :agenda AND atp.profile = :agendaProfile "
		+ "AND atp.isExist = true")
	Optional<AgendaTeamProfile> findByAgendaAndProfileAndIsExistTrue(Agenda agenda, AgendaProfile agendaProfile);
}
