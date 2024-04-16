package gg.repo.game;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.pingpong.game.TeamUser;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
	@Query(value = "select team_user.id, team_user.team_id, team_user.user_id from team, team_user "
		+ "where team.game_id =:gid and team.id = team_user.team_id", nativeQuery = true)
	List<TeamUser> findAllByGameId(@Param("gid") Long gid);

	@Query(value = "select count(*) from game, team, team_user "
		+ "where game.start_time >= :today and team_user.team_id = team.id and team_user.user_id = :userId "
		+ "and team.game_id = game.id and game.status = 'END'", nativeQuery = true)
	Integer findByDateAndUser(@Param("today") LocalDateTime today, @Param("userId") Long userId);

}
