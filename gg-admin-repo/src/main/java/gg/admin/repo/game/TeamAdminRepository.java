package gg.admin.repo.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;

public interface TeamAdminRepository extends JpaRepository<Team, Long> {
	List<Team> findAllByGame(Game game);

}
