package gg.repo.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.game.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
	@Query("select t from Team t where t.game.id=:gameId")
	List<Team> findAllBy(@Param("gameId") Long gameId);
}
