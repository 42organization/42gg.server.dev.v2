package gg.admin.repo.recruit;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;

public interface ApplicationAnswerAdminRepository extends JpaRepository<ApplicationAnswer, Long> {
	@EntityGraph(attributePaths = {"application", "application.applicationAnswers", "application.user"})
	@Query("SELECT aa FROM ApplicationAnswerCheckList aa " +
		"JOIN aa.application a " +
		"JOIN aa.checkList cl " +
		"JOIN aa.application.recruit r " +
		"WHERE r.id =:recruitId AND aa.question.id = :questionId AND cl.id IN :checkListIds")
	Page<ApplicationAnswerCheckList> findAllByCheckList(
		@Param("recruitId") Long recruitId,
		@Param("questionId") Long questionId,
		@Param("checkListIds") List<Long> checkListIds,
		Pageable pageable);
}

