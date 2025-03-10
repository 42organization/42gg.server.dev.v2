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

import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;

@Repository
public interface PrivateScheduleRepository extends JpaRepository<PrivateSchedule, Long> {
	Optional<PrivateSchedule> findByIdAndStatusNot(Long id, ScheduleStatus status);

	List<PrivateSchedule> findByPublicSchedule(PublicSchedule publicSchedule);

	@Query("SELECT ps FROM PrivateSchedule ps JOIN FETCH ps.publicSchedule WHERE ps.publicSchedule = :publicSchedule")
	List<PrivateSchedule> findWithFetchJoinByPublicSchedule(@Param("publicSchedule") PublicSchedule publicSchedule);

	List<PrivateSchedule> findByGroupId(Long groupId);

	@Query("SELECT pr FROM PrivateSchedule pr "
		+ "JOIN pr.publicSchedule pu "
		+ "WHERE NOT (pu.startTime > :endTime OR pu.endTime < :startTime) "
		+ "AND pr.user = :user "
		+ "AND pr.status <> gg.data.calendar.type.ScheduleStatus.DELETE")
	List<PrivateSchedule> findOverlappingSchedulesByUser(LocalDateTime startTime, LocalDateTime endTime, User user);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE PrivateSchedule ps SET ps.status = :status WHERE ps.publicSchedule.id IN "
		+ "(SELECT p.id FROM PublicSchedule p WHERE p.status = :publicStatus)")
	void updateRelatedPrivateSchedules(@Param("status") ScheduleStatus status,
		@Param("publicStatus") ScheduleStatus publicStatus);

	@Query("SELECT ps FROM PrivateSchedule ps "
		+ "JOIN ps.publicSchedule p "
		+ "WHERE ps.alarm = true "
		+ "AND ps.status = :status "
		+ "AND (p.startTime BETWEEN :startOfDay AND :endOfDay OR "
		+ "p.startTime BETWEEN :nextStartOfDay AND :nextEndOfDay)")
	List<PrivateSchedule> findSchedulesWithAlarmForBothDays(@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay,
		@Param("nextStartOfDay") LocalDateTime nextStartOfDay,
		@Param("nextEndOfDay") LocalDateTime nextEndOfDay,
		@Param("status") ScheduleStatus status);

	@Transactional
	@Modifying
	@Query("UPDATE PrivateSchedule ps SET ps.status = :status WHERE ps.publicSchedule = :publicSchedule")
	void bulkUpdateScheduleStatus(@Param("publicSchedule") PublicSchedule publicSchedule,
		@Param("status") ScheduleStatus status);

}
