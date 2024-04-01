package gg.admin.repo.recruit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.application.Application;

public interface ApplicationAdminRepository extends JpaRepository<Application, Long> {
	Optional<Application> findByIdAndRecruitId(Long applicationId, Long recruitId);
}

