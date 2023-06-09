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
}
