package com.gg.server.domain.season.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("select s from Season s where s.startTime <= :now and s.endTime >= :now")
    Optional<Season> findCurrentSeason(@Param("now") LocalDateTime now);
}
