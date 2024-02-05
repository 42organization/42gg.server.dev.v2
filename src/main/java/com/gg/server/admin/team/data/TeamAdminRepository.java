package com.gg.server.admin.team.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.Team;

public interface TeamAdminRepository extends JpaRepository<Team, Long> {
	List<Team> findAllByGame(Game game);

}
