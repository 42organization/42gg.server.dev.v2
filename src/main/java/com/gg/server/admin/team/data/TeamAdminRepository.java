package com.gg.server.admin.team.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.team.data.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamAdminRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByGame(Game game);

}
