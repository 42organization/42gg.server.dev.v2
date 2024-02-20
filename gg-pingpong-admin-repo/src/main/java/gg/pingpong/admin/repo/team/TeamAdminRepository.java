package gg.pingpong.admin.repo.team;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Team;

public interface TeamAdminRepository extends JpaRepository<Team, Long> {
	List<Team> findAllByGame(Game game);

}
