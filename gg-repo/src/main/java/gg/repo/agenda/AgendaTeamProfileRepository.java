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

	@Query("SELECT atp FROM AgendaTeamProfile atp JOIN FETCH atp.profile WHERE atp.agendaTeam = :agendaTeam")
	List<AgendaTeamProfile> findAllByAgendaTeamWithFetchProfile(AgendaTeam agendaTeam);

	@Query("SELECT atp FROM AgendaTeamProfile atp WHERE atp.profile = :agendaProfile AND atp.isExist = true")
	List<AgendaTeamProfile> findByProfileAndIsExistTrue(@Param("agendaProfile") AgendaProfile agendaProfile);

	@Query(
		"SELECT atp FROM AgendaTeamProfile atp "
			+ "WHERE atp.profile = :agendaProfile "
			+ "AND atp.isExist = true "
			+ "AND atp.agenda.status = :status"
	)
	Page<AgendaTeamProfile> findByProfileAndIsExistTrueAndAgendaStatus(
		@Param("agendaProfile") AgendaProfile agendaProfile,
		@Param("status") AgendaStatus status, Pageable pageable);
}
