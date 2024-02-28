package gg.admin.repo.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.game.TeamUser;
import gg.data.user.User;

public interface TeamUserAdminRepository extends JpaRepository<TeamUser, Long> {
	@Query("SELECT tu.user FROM TeamUser tu WHERE tu.team.id = :teamId")
	List<User> findUsersByTeamId(@Param("teamId") Long teamId);

	@Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.team t JOIN FETCH tu.user WHERE tu.team.id IN (:teamId)")
	List<TeamUser> findUsersByTeamIdIn(@Param("teamId") List<Long> teamId);
}
