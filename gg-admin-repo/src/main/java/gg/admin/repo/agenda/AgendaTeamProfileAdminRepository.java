package gg.admin.repo.agenda;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;

@Repository
public interface AgendaTeamProfileAdminRepository extends JpaRepository<AgendaTeamProfile, Long> {

	List<AgendaTeamProfile> findAllByAgendaTeamAndIsExistIsTrue(AgendaTeam agendaTeam);
}
