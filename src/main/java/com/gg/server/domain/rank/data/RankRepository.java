package com.gg.server.domain.rank.data;


import com.gg.server.domain.season.data.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByUserIdAndSeasonId(Long userId, Long seasonId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Rank r where r.season.id=:seasonId")
    void deleteAllBySeasonId(@Param("seasonId") Long seasonId);

    Optional<Rank> findFirstByOrderByCreatedAtDesc();
}
