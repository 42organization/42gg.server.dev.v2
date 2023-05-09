package com.gg.server.domain.rank.data;

import com.gg.server.domain.rank.Rank;
import com.gg.server.domain.season.data.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long> {
    void deleteAllBySeasonId(Long seasonId);

    Optional<Rank> findFirstByOrderByCreatedAtDesc();

}
