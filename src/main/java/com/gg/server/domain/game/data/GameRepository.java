package com.gg.server.domain.game.data;

import com.gg.server.domain.game.dto.GameTeamUserInfo;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;

import java.time.LocalDateTime;
import java.util.Optional;

import com.gg.server.domain.team.dto.GameUser;
import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {
    Slice<Game> findAllByModeAndStatus(Mode mode, StatusType status, Pageable pageable);

    Slice<Game> findAllByAndStatus(StatusType status, Pageable pageable);

    Slice<Game> findAllByAndStatusIn(List<StatusType> statusList, Pageable pageable);

    Slice<Game> findAllByModeAndStatusAndSeasonId(Mode mode, StatusType status, Long season, Pageable pageable);

    @Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, " +
            "t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, t1.image t1Image, t1.total_exp t1Exp, t1.wins t1Wins, t1.losses t1Losses, " +
            "t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, t2.wins t2Wins, t2.losses t2Losses, t2.image t2Image, t2.total_exp t2Exp " +
            "from v_rank_game_detail t1, v_rank_game_detail t2 " +
            "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId order by t1.startTime desc;", nativeQuery = true)
    List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);

    @Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, " +
            "t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, t1.image t1Image, t1.total_exp t1Exp, " +
            "t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, t2.image t2Image, t2.total_exp t2Exp " +
            "from v_teamuser t1, v_teamuser t2 " +
            "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId order by t1.startTime desc;", nativeQuery = true)
    List<GameTeamUser> findTeamsByGameIsInAndNormalMode(@Param("games") List<Long> games);

    @Query(value = "SELECT teamId, gameId, score, startTime, status, mode, userId, intraId, image, total_exp exp" +
            " FROM v_teamuser where gameId = :gameId", nativeQuery = true)
    List<GameTeamUserInfo> findTeamGameUser(@Param("gameId") Long gameId);

    Optional<Game> findByStartTime(LocalDateTime startTime);

    @Query(value = "select g from Game g where g.startTime > :startTime and g.startTime < :endTime")
    List<Game> findAllBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT g FROM Game g, Team t, TeamUser tu WHERE g.startTime > :startTime AND g.startTime < :endTime "
            + "AND g.id = t.game.id AND t.id = tu.team.id AND tu.user.id = :userId")
    Optional<Game> findByUserInSlots(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId);

    @Query(value = "SELECT g FROM Game g, Team t, TeamUser tu WHERE g.status = :status AND g.id = t.game.id"
            + " AND t.id = tu.team.id AND tu.user.id = :userId")
    Optional<Game> findByStatusTypeAndUserId(@Param("status") StatusType status, @Param("userId") Long userId);

    @Query(value = "select gameId " +
            "from v_teamuser " +
            "where intraId = :intra and status in (:status)", nativeQuery = true)
    Slice<Long> findGamesByUser(@Param("intra") String intra, @Param("status") List<String> status, Pageable pageable);

    @Query(value = "select gameId " +
            "from v_teamuser " +
            "where intraId = :intra and mode in (:mode) and status=:status", nativeQuery = true)
    Slice<Long> findGamesByUserAndMode(@Param("intra") String intra, @Param("mode") String mode, @Param("status") String status, Pageable pageable);

    @Query(value = "select gameId " +
            "from v_teamuser " +
            "where intraId = :intra and mode in (:mode) and seasonId = :seasonId and status=:status", nativeQuery = true)
    Slice<Long> findGamesByUserAndModeAndSeason(@Param("intra") String intra, @Param("mode") String mode, @Param("seasonId") Long seasonId, @Param("status") String status, Pageable pageable);

    List<Game> findAllByStatusAndStartTimeLessThanEqual(StatusType status, LocalDateTime startTime);

    List<Game> findAllByStatusAndEndTimeLessThanEqual(StatusType status, LocalDateTime endTime);

    @Query(value = "SELECT u.id userId, u.e_mail email, u.intra_id intraId, u.sns_noti_opt snsNotiOpt, g.id gameId " +
            "FROM " +
            "(SELECT id, status FROM game where start_time<=:time) g, " +
            "team t, team_user tu, user u " +
            "WHERE g.id=t.game_id AND t.id = tu.team_id AND tu.user_id=u.id AND g.status = 'BEFORE'", nativeQuery = true)
    List<GameUser> findAllByStartTimeLessThanEqual(@Param("time") LocalDateTime time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Game> findWithPessimisticLockById(Long gameId);

    @Override
    @EntityGraph(attributePaths = {"season"})
    Optional<Game> findById(Long gameId);
}
