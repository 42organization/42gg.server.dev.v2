package gg.repo.recruit.user.application;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.application.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	@EntityGraph(attributePaths = {"recruit"})
	List<Application> findAllByUserId(Long userId);
}
