package com.gg.server.domain.user.data;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);

    Optional<User> getUserByIntraId(String IntraId);

    Page<User> findByIntraIdContains(Pageable pageable, String intraId);

    Page<User> findAllByTotalExpGreaterThan(Pageable pageable, Integer exp);

    Optional<User> findByKakaoId(Long kakaoId);

    @Query(nativeQuery = true, value = "select ranking from " +
            "(select intra_id, row_number() over (order by total_exp desc, intra_id asc) as ranking from user) ranked where intra_id=:intraId")
    Long findExpRankingByIntraId(@Param("intraId") String intraId);

    Page<User> findAll(Pageable pageable);

    @Query("select tu.user from User u, TeamUser tu, Team t, Game g" +
            " where g.id=:gameId and t.game.id =g.id and tu.team.id = t.id "
            + "and u.id = tu.user.id and u.id !=:userId")
    List<User> findEnemyByGameAndUser(@Param("gameId") Long gameId, @Param("userId") Long userId);

    List<User> findUsersByIdIn(List<Long> userIds);
}
