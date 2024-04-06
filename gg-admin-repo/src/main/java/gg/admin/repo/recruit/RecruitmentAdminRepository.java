package gg.admin.repo.recruit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.recruitment.Recruitment;

public interface RecruitmentAdminRepository extends JpaRepository<Recruitment, Long> {
	Page<Recruitment> findAllByOrderByEndTimeDesc(Pageable pageable);
}
