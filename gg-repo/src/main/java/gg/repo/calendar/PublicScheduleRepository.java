package gg.repo.calendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;

@Repository
public interface PublicScheduleRepository extends JpaRepository<PublicSchedule, Long> {
	List<PublicSchedule> findByAuthor(String author);

	List<PublicSchedule> findByEndTimeGreaterThanEqualAndStartTimeLessThanEqual(LocalDateTime startTime,
		LocalDateTime endTime);

	boolean existsByTitleAndStartTime(String title, LocalDateTime beginAt);

	Optional<PublicSchedule> findByIdAndStatusNot(Long id, ScheduleStatus status);

	List<PublicSchedule> findByEndTimeGreaterThanEqualAndStartTimeLessThanEqualAndClassificationNotAndStatusNot(
		LocalDateTime start, LocalDateTime end, DetailClassification classification, ScheduleStatus status);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE PublicSchedule ps SET ps.status = :status WHERE ps.status = :currentStatus AND ps.endTime < :time")
	void updateExpiredPublicSchedules(@Param("status") ScheduleStatus status,
		@Param("currentStatus") ScheduleStatus currentStatus,
		@Param("time") LocalDateTime time);

	@Modifying
	@Query("UPDATE PublicSchedule p SET p.sharedCount = p.sharedCount + 1 WHERE p.id = :scheduleId")
	void incrementSharedCount(@Param("scheduleId") Long scheduleId);

}
