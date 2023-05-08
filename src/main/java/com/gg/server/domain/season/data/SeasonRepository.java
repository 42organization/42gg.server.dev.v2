package com.gg.server.domain.season.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime now);
}
