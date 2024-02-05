package com.gg.server.domain.game.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.game.type.StatusType;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.GameTeamUserInfo;
import com.gg.server.domain.team.dto.GameUser;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {
	Slice<Game> findAllByModeAndStatus(Mode mode, StatusType status, Pageable pageable);

	// Slice<Game> findAllByStatus(StatusType status, Pageable pageable);

	// Slice<Game> findAllByStatusIn(List<StatusType> statusList, Pageable pageable);

	Slice<Game> findAllByModeAndStatusAndSeasonId(Mode mode, StatusType status, Long season, Pageable pageable);

	Slice<Game> findAllByModeInAndStatusIn(List<Mode> modeList, List<StatusType> statusList, Pageable pageable);

	Slice<Game> findAllByModeInAndStatus(List<Mode> modeList, StatusType status, Pageable pageable);

	@Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, "
		+ "t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, t1.image t1Image, t1.total_exp t1Exp, "
		+ "t1.wins t1Wins, t1.losses t1Losses, "
		+ "t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, t2.wins t2Wins, t2.losses t2Losses, "
		+ "t2.image t2Image, t2.total_exp t2Exp "
		+ "from v_rank_game_detail t1, v_rank_game_detail t2 "
		+ "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId "
		+ "order by t1.startTime desc;", nativeQuery = true)
	List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);

	@Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, "
		+ "t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, t1.image t1Image, t1.total_exp t1Exp, "
		+ "t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, t2.image t2Image, t2.total_exp t2Exp "
		+ "from v_teamuser t1, v_teamuser t2 "
		+ "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId "
		+ "order by t1.startTime desc;", nativeQuery = true)
	List<GameTeamUser> findTeamsByGameIsInAndNormalMode(@Param("games") List<Long> games);

	@Query(value = "select t1.gameId, t1.startTime, t1.status, t1.mode, "
		+ "t1.intraId t1IntraId, t1.teamId t1TeamId, t1.win t1IsWin, t1.score t1Score, "
		+ "t1.image t1Image, t1.total_exp t1Exp, t1.wins t1Wins, t1.losses t1Losses, "
		+ "t2.win t2IsWin, t2.teamId t2TeamId, t2.score t2Score, t2.intraId t2IntraId, "
		+ "t2.wins t2Wins, t2.losses t2Losses, t2.image t2Image, t2.total_exp t2Exp "
		+ "from v_rank_game_detail t1, v_rank_game_detail t2 "
		+ "where t1.gameId = (:gameId) and t1.teamId <t2.teamId and t1.gameId=t2.gameId "
		+ "order by t1.startTime desc;", nativeQuery = true)
	Optional<GameTeamUser> findTeamsByGameId(@Param("gameId") Long gameId);

	@Query(value = "SELECT teamId, gameId, score, startTime, status, mode, userId, intraId, image, total_exp exp"
		+ " FROM v_teamuser where gameId = :gameId", nativeQuery = true)
	List<GameTeamUserInfo> findTeamGameUser(@Param("gameId") Long gameId);

	Optional<Game> findByStartTime(LocalDateTime startTime);

	@Query(value = "select g from Game g where g.startTime > :startTime and g.startTime < :endTime")
	List<Game> findAllBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	@Query(value = "select g from Game g where (g.startTime between :startTime and :endTime) "
		+ "or (g.endTime between :startTime and :endTime) "
		+ "or (:startTime between g.startTime and g.endTime) "
		+ "or (:endTime between g.startTime and g.endTime)")
	List<Game> findAllBetweenTournament(@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

	@Query(value =
		"SELECT g FROM Game g, Team t, TeamUser tu WHERE g.startTime > :startTime AND g.startTime < :endTime "
			+ "AND g.id = t.game.id AND t.id = tu.team.id AND tu.user.id = :userId")
	Optional<Game> findByUserInSlots(@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime, @Param("userId") Long userId);

	@Query(value = "SELECT g FROM Game g, Team t, TeamUser tu WHERE g.status = :status AND g.id = t.game.id"
		+ " AND t.id = tu.team.id AND tu.user.id = :userId")
	Optional<Game> findByStatusTypeAndUserId(@Param("status") StatusType status, @Param("userId") Long userId);

	@Query(value = "select gameId "
		+ "from v_teamuser "
		+ "where intraId = :intra and mode in (:mode) and status in(:status)", nativeQuery = true)
	Slice<Long> findGamesByUserAndModeInAndStatusIn(@Param("intra") String intra, @Param("mode") List<String> mode,
		@Param("status") List<String> status, Pageable pageable);

	@Query(value = "select gameId "
		+ "from v_teamuser "
		+ "where intraId=:intra and mode=:mode and status=:status", nativeQuery = true)
	Slice<Long> findGamesByUserAndModeAndStatus(@Param("intra") String intra, @Param("mode") String mode,
		@Param("status") String status, Pageable pageable);

	@Query(value = "select gameId "
		+ "from v_teamuser "
		+ "where intraId = :intra and mode=:mode and seasonId = :seasonId and status = :status", nativeQuery = true)
	Slice<Long> findGamesByUserAndModeAndSeason(@Param("intra") String intra, @Param("mode") String mode,
		@Param("seasonId") Long seasonId, @Param("status") String status, Pageable pageable);

	List<Game> findAllByStatusAndStartTimeLessThanEqual(StatusType status, LocalDateTime startTime);

	List<Game> findAllByStatusAndEndTimeLessThanEqual(StatusType status, LocalDateTime endTime);

	@Query(value = "SELECT u.id userId, u.e_mail email, u.intra_id intraId, u.sns_noti_opt snsNotiOpt, g.id gameId "
		+ "FROM "
		+ "(SELECT id, status FROM game where start_time<=:time) g, "
		+ "team t, team_user tu, user u "
		+ "WHERE g.id=t.game_id AND t.id = tu.team_id AND tu.user_id=u.id AND g.status = 'BEFORE'", nativeQuery = true)
	List<GameUser> findAllByStartTimeLessThanEqual(@Param("time") LocalDateTime time);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Game> findWithPessimisticLockById(Long gameId);

	@Override
	@EntityGraph(attributePaths = {"season"})
	Optional<Game> findById(Long gameId);
}
