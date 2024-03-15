package gg.repo.recruit.user.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.ApplicationAnswer;

public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {

	@Query("SELECT a FROM ApplicationAnswer a JOIN FETCH a.question WHERE a.application.user.id = :userId "
		+ "AND a.application.recruit.id = :recruitId AND a.application.id = :applicationId order by a.question.sortNum")
	List<ApplicationAnswer> findAllAnswers(@Param("userId") Long userId,
		@Param("recruitId") Long recruitId, @Param("applicationId") Long applicationId);
}
