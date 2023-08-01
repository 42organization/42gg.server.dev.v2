package com.gg.server.domain.team.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    @Query(value = "select team_user.id, team_user.team_id, team_user.user_id from team, team_user " +
            "where team.game_id =:gid and team.id = team_user.team_id", nativeQuery = true)
    List<TeamUser> findAllByGameId(Long gid);

    @Query(value = "select count(*) from game, team, team_user " +
            "where game.start_time >= :today and team_user.team_id = team.id and team_user.user_id = :userId " +
            "and team.game_id = game.id and game.status = 'END'", nativeQuery = true)
    Integer findByDateAndUser(LocalDateTime today, Long userId);

}
