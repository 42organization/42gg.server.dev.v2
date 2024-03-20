package gg.admin.repo.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.admin.repo.game.out.GameTeamUser;
import gg.data.game.Game;
import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.season.Season;

public interface GameAdminRepository extends JpaRepository<Game, Long> {

	Page<Game> findBySeason(Pageable pageable, Season season);

	Page<Game> findBySeasonAndModeIn(Pageable pageable, Season season, List<Mode> modes);

	Page<Game> findAllByModeIn(Pageable pageable, List<Mode> modes);

	@Query(value = "select t1.gameId, t1.startTime, t1.endTime, t1.status, t1.mode, "
		+ "t1.teamId t1TeamId, t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, "
		+ "t1.image t1Image, t1.total_exp t1Exp, t1.wins t1Wins, t1.losses t1Losses, "
		+ "t2.teamId t2TeamId, t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, "
		+ "t2.wins t2Wins, t2.losses t2Losses, t2.image t2Image, t2.total_exp t2Exp "
		+ "from v_rank_game_detail t1, v_rank_game_detail t2 "
		+ "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId "
		+ "order by t1.startTime desc;", nativeQuery = true)
	List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);

	@Query(value = "SELECT g FROM Game g "
		+ "INNER JOIN Team t ON g.id = t.game.id "
		+ "INNER JOIN TeamUser tu ON tu.team.id = t.id "
		+ "WHERE g.status = :status AND tu.user.id = :userId")
	Optional<Game> findByStatusTypeAndUserId(@Param("status") StatusType status, @Param("userId") Long userId);

	@Query(value = "SELECT g FROM Game g JOIN FETCH g.season WHERE g.id = :gameId")
	Optional<Game> findGameWithSeasonByGameId(@Param("gameId") Long gameId);
}
