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

	@Query("SELECT p FROM PublicSchedule p WHERE p.id = :id AND p.status != :status")
	Optional<PublicSchedule> findByIdAndStatusNot(@Param("id") Long id, @Param("status") ScheduleStatus status);

	boolean existsByTitleAndStartTime(String title, LocalDateTime beginAt);

	@Query("SELECT p FROM PublicSchedule p WHERE p.endTime >= :start AND p.startTime <= :end "
		+ "AND p.classification != :classification AND p.status != :scheduleStatus")
	List<PublicSchedule> findByEndTimeGreaterThanEqualAndStartTimeLessThanEqualAndClassificationNotAndStatusNot(
		@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end,
		@Param("classification") DetailClassification classification,
		@Param("scheduleStatus") ScheduleStatus scheduleStatus);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE PublicSchedule ps SET ps.status = :status WHERE ps.status = :currentStatus AND ps.endTime < :time")
	void updateExpiredPublicSchedules(@Param("status") ScheduleStatus status,
		@Param("currentStatus") ScheduleStatus currentStatus,
		@Param("time") LocalDateTime time);
}
