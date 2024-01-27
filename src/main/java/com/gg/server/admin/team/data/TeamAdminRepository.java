package com.gg.server.admin.team.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.team.data.Team;

public interface TeamAdminRepository extends JpaRepository<Team, Long> {
	List<Team> findAllByGame(Game game);

}
