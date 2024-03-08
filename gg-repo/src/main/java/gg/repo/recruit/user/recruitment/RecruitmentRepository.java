package gg.repo.recruit.user.recruitment;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.recruit.recruitment.Recruitments;

public interface RecruitmentRepository extends JpaRepository<Recruitments, Long>, RecruitmentRepositoryCustom {
	@Query("SELECT r FROM Recruitments r "
		+ "WHERE r.isDeleted = false AND r.startTime <= :date AND r.isFinish = false "
		+ "ORDER BY r.startTime DESC")
	Page<Recruitments> findActiveRecruitmentList(LocalDateTime date, Pageable pageable);
}
