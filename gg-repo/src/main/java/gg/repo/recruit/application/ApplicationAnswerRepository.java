package gg.repo.recruit.application;

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

	@Query("SELECT a FROM ApplicationAnswer a WHERE a.application.user.id = :userId "
		+ "AND a.application.recruit.id = :recruitmentId "
		+ "AND a.application.id = :applicationId "
		+ "AND a.question.id IN :questionIds")
	List<ApplicationAnswer> findAllByQuestionIds(
		@Param("userId") Long userId,
		@Param("applicationId") Long applicationId,
		@Param("recruitmentId") Long recruitmentId,
		@Param("questionIds") List<Long> questionIds);
}
