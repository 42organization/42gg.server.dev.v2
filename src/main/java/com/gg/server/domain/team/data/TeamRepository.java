package com.gg.server.domain.team.data;

import java.time.LocalDateTime;
import java.util.List;

import com.gg.server.domain.team.dto.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t where t.game.id=:gameId")
    List<Team> findAllBy(@Param("gameId") Long gameId);

    @Query(value = "SELECT u.id userId, u.e_mail email, u.intra_id intraId, u.sns_noti_opt snsNotiOpt, g.id gameId " +
            "FROM " +
            "(SELECT id FROM game where start_time=:time) g, " +
            "team t, team_user tu, user u " +
            "WHERE g.id=t.game_id AND t.id = tu.team_id AND tu.user_id=u.id", nativeQuery = true)
    List<GameUser> findAllByStartTimeEquals(@Param("time") LocalDateTime time);
}
