package com.gg.server.domain.user.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.user.User;
import com.gg.server.domain.rank.dto.ExpRankV2Dto;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByIntraId(String intraId);

	Optional<User> getUserByIntraId(String intraId);

	Page<User> findByIntraIdContains(Pageable pageable, String intraId);

	Page<User> findAllByTotalExpGreaterThan(Pageable pageable, Integer exp);

	Optional<User> findByKakaoId(Long kakaoId);

	@Query(nativeQuery = true, value = "select ranking from "
		+ "(select intra_id, row_number() over (order by total_exp desc, intra_id asc) as ranking from user) ranked "
		+ "where intra_id=:intraId")
	Long findExpRankingByIntraId(@Param("intraId") String intraId);

	Page<User> findAll(Pageable pageable);

	@Query("select tu.user from User u, TeamUser tu, Team t, Game g"
		+ " where g.id=:gameId and t.game.id =g.id and tu.team.id = t.id "
		+ "and u.id = tu.user.id and u.id !=:userId")
	List<User> findEnemyByGameAndUser(@Param("gameId") Long gameId, @Param("userId") Long userId);

	List<User> findUsersByIdIn(List<Long> userIds);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.imageUri = :imageUri where u.id = :id")
	void updateUserImage(Long id, String imageUri);

	@Query(value = "SELECT u.intra_id intraId, r.status_message statusMessage, u.total_exp totalExp, "
		+ "u.image_uri imageUri, u.text_color textColor, "
		+ "RANK() OVER(ORDER BY u.total_exp DESC, r.modified_at DESC, r.ppp DESC) ranking "
		+ "FROM User u LEFT JOIN Ranks r "
		+ "ON u.id = r.user_id "
		+ "WHERE r.season_id = :seasonId AND u.total_exp > 0 "
		+ "LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<ExpRankV2Dto> findExpRank(@Param("offset") int offset, @Param("limit") int limit,
		@Param("seasonId") Long seasonId);

}
