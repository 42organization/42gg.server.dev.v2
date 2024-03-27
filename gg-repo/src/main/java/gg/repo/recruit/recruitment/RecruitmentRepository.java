package gg.repo.recruit.recruitment;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.recruitment.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentRepositoryCustom {
	@Query("SELECT r FROM Recruitment r "
		+ "WHERE r.isDeleted = false AND r.startTime <= :date AND r.isFinish = false "
		+ "ORDER BY r.startTime DESC")
	Page<Recruitment> findActiveRecruitmentList(@Param("date") LocalDateTime date, Pageable pageable);

	@Query("SELECT r FROM Recruitment r "
		+ "WHERE r.id = :recruitId AND r.startTime <= :date AND r.isDeleted = false AND r.isFinish = false")
	Optional<Recruitment> findByActiveRecruit(@Param("recruitId") Long recruitId, @Param("date") LocalDateTime date);
}
