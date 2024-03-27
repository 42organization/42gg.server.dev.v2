package gg.repo.recruit.application;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.RecruitStatus;

public interface RecruitStatusRepository extends JpaRepository<RecruitStatus, Long> {

	@Query("SELECT rs FROM RecruitStatus rs WHERE rs.application.id = :applicationId")
	Optional<RecruitStatus> findByApplicationId(@Param("applicationId") Long applicationId);
}
