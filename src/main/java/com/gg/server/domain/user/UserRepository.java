package com.gg.server.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);
    User getUserByIntraId(String IntraId);
    Page<User> findByIntraIdContains(Pageable pageable, String intraId);
    Optional<User> findByKakaoId(Long kakaoId);
    @Query(nativeQuery = true, value = "select ranking from " +
            "(select intra_id, row_number() over (order by total_exp desc) as ranking from user) ranked where intra_id=:intraId")
    Long findExpRankingByIntraId(@Param("intraId") String intraId);

    Page<User> findAll(Pageable pageable);
}
