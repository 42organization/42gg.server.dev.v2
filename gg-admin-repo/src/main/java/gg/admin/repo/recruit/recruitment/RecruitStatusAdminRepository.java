package gg.admin.repo.recruit.recruitment;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.RecruitStatus;

public interface RecruitStatusAdminRepository extends JpaRepository<RecruitStatus, Long> {
	@Query("SELECT rs FROM RecruitStatus rs WHERE rs.application.recruit.id = :recruitmentId AND rs.interviewDate BETWEEN :startDate AND :endDate")
	Optional<RecruitStatus> findFirstByRecruitmentIdAndInterviewDateBetween(@Param("recruitmentId") Long recruitmentId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);
}
