package com.gg.server.admin.penalty.data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.manage.Penalty;

public interface PenaltyAdminRepository extends JpaRepository<Penalty, Long>, PenaltyAdminRepositoryCustom {
	@EntityGraph(attributePaths = {"user"})
	Page<Penalty> findAll(Pageable pageable);

	@Query("select p from Penalty p where p.user.id = :userId and p.startTime > :startTime")
	List<Penalty> findAfterPenaltiesByUser(@Param("userId") Long userId,
		@Param("startTime") LocalDateTime startTime);
}
