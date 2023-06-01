package com.gg.server.admin.game.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.season.data.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameAdminRepository extends JpaRepository<Game, Long> {

    Page<Game> findBySeason(Pageable pageable, Season season);

    @Query(value = "select t1.gameId, t1.startTime, t1.endTime, t1.status, t1.mode, " +
            "t1.teamId t1TeamId, t1.intraId t1IntraId, t1.win t1IsWin, t1.score t1Score, t1.image t1Image, t1.total_exp t1Exp, t1.wins t1Wins, t1.losses t1Losses, " +
            "t2.teamId t2TeamId, t2.win t2IsWin, t2.score t2Score, t2.intraId t2IntraId, t2.wins t2Wins, t2.losses t2Losses, t2.image t2Image, t2.total_exp t2Exp " +
            "from v_rank_game_detail t1, v_rank_game_detail t2 " +
            "where t1.gameId IN (:games) and t1.teamId <t2.teamId and t1.gameId=t2.gameId order by t1.startTime desc;", nativeQuery = true)
    List<GameTeamUser> findTeamsByGameIsIn(@Param("games") List<Long> games);
}
