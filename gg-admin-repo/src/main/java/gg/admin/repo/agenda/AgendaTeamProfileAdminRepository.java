package gg.admin.repo.agenda;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaTeamProfileAdminRepository extends JpaRepository<AgendaTeamProfile, Long> {

	List<AgendaTeamProfile> findAllByAgendaTeamAndIsExistIsTrue(AgendaTeam agendaTeam);
}
