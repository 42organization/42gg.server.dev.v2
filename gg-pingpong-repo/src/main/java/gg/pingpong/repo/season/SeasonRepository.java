package gg.pingpong.repo.season;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.pingpong.data.season.Season;

public interface SeasonRepository extends JpaRepository<Season, Long> {
	@Query("select s from Season s where s.startTime <= :now and s.endTime >= :now")
	Optional<Season> findCurrentSeason(@Param("now") LocalDateTime now);

	@Query("select s from Season s where s.startTime <= :now")
	List<Season> findActiveSeasons(@Param("now") LocalDateTime now);

	@Query("select s from Season s where s.startTime <= :now order by s.startTime desc")
	List<Season> findActiveSeasonsDesc(@Param("now") LocalDateTime now);

	@Query("select s from Season s where s.startTime <= :now and s.endTime >= :now or s.startTime > :now")
	List<Season> findCurrentAndNewSeason(@Param("now") LocalDateTime now);

}
