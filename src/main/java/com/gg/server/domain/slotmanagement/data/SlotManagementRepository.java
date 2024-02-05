package com.gg.server.domain.slotmanagement.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.domain.slotmanagement.SlotManagement;

public interface SlotManagementRepository
	extends JpaRepository<SlotManagement, Long> {
	@Query("select sm from SlotManagement sm where sm.endTime > :now or sm.endTime is null")
	List<SlotManagement> findAfter(@Param("now") LocalDateTime now);

	@Query("select sm from SlotManagement sm where (sm.endTime is null"
		+ " or sm.endTime > :now) and sm.startTime <=:now")
	Optional<SlotManagement> findCurrent(@Param("now") LocalDateTime now);

	SlotManagement findFirstByOrderByCreatedAtDesc();

}
