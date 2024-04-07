package gg.admin.repo.recruit;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.recruit.recruitment.Recruitment;

public interface RecruitmentAdminRepository extends JpaRepository<Recruitment, Long> {
	Page<Recruitment> findAllByOrderByEndTimeDesc(Pageable pageable);

	@Query("SELECT r FROM Recruitment r WHERE r.id = :recruitId AND r.isDeleted = false")
	Optional<Recruitment> findNotDeletedRecruit(Long recruitId);
}
