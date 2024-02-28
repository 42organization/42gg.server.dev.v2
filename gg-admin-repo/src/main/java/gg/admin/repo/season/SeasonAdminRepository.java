package gg.admin.repo.season;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.season.Season;

public interface SeasonAdminRepository extends JpaRepository<Season, Long> {
	/* 입력시간과 모드로 입력시간 이후 가장 가까운 시즌 가져오기 */
	@Query("SELECT e FROM Season e WHERE e.startTime > :targetTime ORDER BY e.startTime ASC")
	List<Season> findAfterSeasons(@Param("targetTime") LocalDateTime targetTime);

	@Query("SELECT e FROM Season e WHERE e.startTime < :targetTime ORDER BY e.startTime DESC")
	List<Season> findBeforeSeasons(@Param("targetTime") LocalDateTime targetTime);

	@Query("select s from Season s where s.startTime <= :now and s.endTime >= :now")
	Optional<Season> findCurrentSeason(@Param("now") LocalDateTime now);

	List<Season> findAllByOrderByStartTimeDesc();

	List<Season> findAllByOrderByStartTimeAsc();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "UPDATE Season s "
		+ "SET s.seasonName = :seasonName, s.startTime = :startTime, "
		+ "s.startPpp = :startPpp, s.pppGap = :pppGap "
		+ "WHERE s.id = :id")
	void updateReserveSeasonById(@Param("id") Long seasonId, @Param("seasonName") String seasonName,
		@Param("startTime") LocalDateTime startTime,
		@Param("startPpp") Integer startPpp, @Param("pppGap") Integer pppGap);
}
