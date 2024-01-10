package com.gg.server.domain.rank.data;

import com.gg.server.domain.rank.dto.RankV2Dto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByUserIdAndSeasonId(Long userId, Long seasonId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Rank r where r.season.id=:seasonId")
    void deleteAllBySeasonId(@Param("seasonId") Long seasonId);

    Optional<Rank> findFirstByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"user", "tier"})
    List<Rank> findAllBySeasonId(Long seasonId);

    List<Rank> findAllBySeasonIdOrderByPppDesc(Long seasonId);

    @Query(value = "select count(r) from Rank r where r.season.id=:seasonId and not (r.wins = 0 and r.losses = 0)")
    Long countRealRankPlayers(@Param("seasonId") Long seasonId);

    @Query(value = "SELECT u.intra_id intraId, r.status_message statusMessage, r.ppp, "
            + "t.image_uri tierImageUri, u.text_color textColor, "
            + "RANK() OVER(ORDER BY r.ppp DESC, pg.created_at ASC, u.total_exp DESC) AS ranking "
            + "FROM Ranks r "
            + "INNER JOIN Tier t "
            + "ON r.tier_id = t.id "
            + "INNER JOIN User u "
            + "ON r.user_id = u.id "
            + "INNER JOIN (SELECT MAX(p.created_at) created_at, p.user_id user_id "
            + "                FROM PChange p"
            + "                INNER JOIN Game g"
            + "                ON p.game_id = g.id"
            + "            WHERE g.season_id = :seasonId"
            + "            GROUP BY p.user_id) pg "
            + "ON pg.user_id = u.id "
            + "WHERE r.season_id = :seasonId AND (r.losses > 0 OR r.wins > 0) "
            + "LIMIT :limit OFFSET :offset ", nativeQuery = true)
    List<RankV2Dto> findPppRankBySeasonId(@Param("offset")int offset, @Param("limit")int limit, @Param("seasonId") Long seasonId);

    @Query(value = "SELECT count(*) "
            + "FROM Ranks r "
            + "INNER JOIN User u "
            + "ON r.user_id = u.id "
            + "WHERE r.season_id = :seasonId AND (r.losses > 0 OR r.wins > 0) ", nativeQuery = true)
    int countRankUserBySeasonId(@Param("seasonId")Long seasonId);

    @Query(value = "SELECT ranked.ranking "
            + "FROM ("
                + "SELECT u.id userId, RANK() OVER(ORDER BY r.ppp DESC, pg.created_at ASC, u.total_exp DESC) AS ranking "
                + "FROM Ranks r "
                + "INNER JOIN User u "
                + "ON r.user_id = u.id "
                + "INNER JOIN (SELECT MAX(p.created_at) created_at, p.user_id user_id "
                + "                FROM PChange p"
                + "                INNER JOIN Game g"
                + "                ON p.game_id = g.id"
                + "            WHERE g.season_id = :seasonId"
                + "            GROUP BY p.user_id) pg "
                + "ON pg.user_id = u.id "
                + "WHERE r.season_id = :seasonId AND (r.losses > 0 OR r.wins > 0) "
            + ") ranked "
            + "WHERE ranked.userId = :userId", nativeQuery = true)
    Optional<Integer> findRankByUserIdAndSeasonId(@Param("userId")Long userId, @Param("seasonId")Long seasonId);
}
