package com.gg.server.admin.team.data;

import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserAdminRepository extends JpaRepository<TeamUser, Long> {
    @Query("SELECT tu.user FROM TeamUser tu WHERE tu.team.id = :teamId")
    List<User> findUsersByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.team t JOIN FETCH tu.user WHERE tu.team.id IN (:teamId)")
    List<TeamUser> findUsersByTeamIdIn(@Param("teamId") List<Long> teamId);
}
