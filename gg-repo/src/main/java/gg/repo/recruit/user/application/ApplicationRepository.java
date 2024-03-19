package gg.repo.recruit.user.application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	@EntityGraph(attributePaths = {"recruit"})
	@Query("SELECT a FROM Application a WHERE a.user.id = :userId and a.isDeleted = false")
	List<Application> findAllByUserId(Long userId);

	@Query("SELECT a FROM Application a "
		+ "JOIN FETCH a.recruit r "
		+ "WHERE a.user.id = :userId and r.id = :recruitId and a.isDeleted = false and r.isDeleted = false")
	Optional<Application> findByUserIdAndRecruitId(@Param("userId") Long userId, @Param("recruitId") Long recruitId);

	@Query("SELECT a FROM Application a "
		+ "JOIN FETCH a.recruit r "
		+ "WHERE a.id = :applicationId and a.user.id = :userId "
		+ "and r.id = :recruitmentId and a.isDeleted = false and r.isDeleted = false")
	Optional<Application> findApplication(@Param("applicationId") Long applicationId, @Param("userId")Long userId,
		@Param("recruitmentId")Long recruitmentId);
}
