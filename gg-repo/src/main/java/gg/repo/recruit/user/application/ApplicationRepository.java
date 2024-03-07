package gg.repo.recruit.user.application;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.recruit.application.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	@EntityGraph(attributePaths = {"recruit"})
	@Query("SELECT a FROM Application a WHERE a.user.id = :userId and a.isDeleted = false")
	List<Application> findAllByUserId(Long userId);
}
