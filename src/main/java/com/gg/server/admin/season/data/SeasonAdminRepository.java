package com.gg.server.admin.season.data;

import com.gg.server.domain.season.data.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeasonAdminRepository extends JpaRepository<Season, Long> {
    /* 입력시간과 모드로 입력시간 이후 가장 가까운 시즌 가져오기 */
    @Query("SELECT e FROM Season e WHERE e.startTime > :targetTime ORDER BY e.startTime ASC")
    List<Season> findAfterSeasons(@Param("targetTime") LocalDateTime targetTime);
    @Query("SELECT e FROM Season e WHERE e.startTime < :targetTime ORDER BY e.startTime DESC")
    List<Season> findBeforeSeasons(@Param("targetTime") LocalDateTime targetTime);
    @Query("select s from Season s where s.startTime <= :now and s.endTime >= :now")
    Optional<Season> findCurrentSeason(@Param("now") LocalDateTime now);

}
