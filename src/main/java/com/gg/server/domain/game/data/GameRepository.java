package com.gg.server.domain.game.data;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom{
    Slice<Game> findAllByModeAndStatus(Mode mode, StatusType status, Pageable pageable);
    Slice<Game> findAllByAndStatus(StatusType status, Pageable pageable);
    Slice<Game> findAllByAndStatusIn(List<StatusType> statusList, Pageable pageable);
    Slice<Game> findAllByModeAndStatusAndSeasonId(Mode mode, StatusType status, Long season, Pageable pageable);
    @Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, t1.intraId t1IntraId, t1.image t1Image, t1.total_exp t1Exp, " +
            "t2.intraId t2IntraId, t2.image t2Image, t2.total_exp t2Exp " +
            "from v_teamuser t1, v_teamuser t2 " +
            "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId order by t1.startTime desc;", nativeQuery = true)
    List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);
    Optional<Game> findByStartTime(LocalDateTime startTime);
    @Query(value = "select g from Game g where g.startTime > :startTime and g.startTime < :endTime")
    List<Game> findAllBetween(@Param("startTime")LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    @Query(value = "SELECT g FROM Game g, Team t, TeamUser tu WHERE g.startTime > :startTime AND g.startTime < :endTime "
            + "AND g.id = t.game.id AND t.id = tu.team.id AND tu.user.id = :userId")
    Optional<Game> findByUserInSlots(@Param("startTime")LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId);
    @Query(value = "SELECT g FROM Game g, Team t, TeamUser tu WHERE g.status = :status AND g.id = t.game.id"
            + " AND t.id = tu.team.id AND tu.user.id = :userId")
    Optional<Game> findByStatusTypeAndUserId(@Param("status") StatusType status, @Param("userId") Long userId);
}
