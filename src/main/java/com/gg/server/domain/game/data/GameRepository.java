package com.gg.server.domain.game.data;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom{
    Slice<Game> findAllByModeAndStatusIsInOrderByStartTimeDesc(Mode mode, List<StatusType> status, Pageable pageable);
    @Query(value = "select t1.gameId, t1.intraId t1IntraId, t1.image t1Image, t1.total_exp t1Exp, " +
            "t2.intraId t2IntraId, t2.image t2Image, t2.total_exp t2Exp " +
            "from v_teamuser t1, v_teamuser t2 " +
            "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId order by t1.gameId asc, t1.startTime desc;", nativeQuery = true)
    List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);
}
