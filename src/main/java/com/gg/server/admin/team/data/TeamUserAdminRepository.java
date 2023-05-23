package com.gg.server.admin.team.data;

import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamUserAdminRepository extends JpaRepository<TeamUser, Long> {
    @Query("SELECT tu.user FROM TeamUser tu WHERE tu.team.id = :teamId")
    List<User> findUsersByTeamId(Long teamId);
}
