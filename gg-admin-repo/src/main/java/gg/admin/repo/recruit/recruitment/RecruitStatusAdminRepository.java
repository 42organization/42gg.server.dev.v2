package gg.admin.repo.recruit.recruitment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.RecruitStatus;

public interface RecruitStatusAdminRepository extends JpaRepository<RecruitStatus, Long> {
	@Query("SELECT rs FROM RecruitStatus rs WHERE rs.application.recruit.id = :recruitmentId AND "
		+ "rs.interviewDate BETWEEN :startDate AND :endDate")
	List<RecruitStatus> findFirstByRecruitmentIdAndInterviewDateBetween(@Param("recruitmentId") Long recruitmentId,
		@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

	default boolean existsByRecruitmentIdAndInterviewDateBetween(Long recruitmentId, LocalDateTime startDate,
		LocalDateTime endDate) {
		return findFirstByRecruitmentIdAndInterviewDateBetween(recruitmentId, startDate, endDate,
			PageRequest.of(0, 1)).size() == 1;
	}
}
