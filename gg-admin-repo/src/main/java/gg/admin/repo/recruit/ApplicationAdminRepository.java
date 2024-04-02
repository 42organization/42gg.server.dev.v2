package gg.admin.repo.recruit;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.application.Application;

public interface ApplicationAdminRepository extends JpaRepository<Application, Long> {
	Optional<Application> findByIdAndRecruitId(Long applicationId, Long recruitId);

	@EntityGraph(attributePaths = {"user", "applicationAnswers", "applicationAnswers.question"})
	Page<Application> findByRecruitIdAndIsDeletedFalse(Long recruitId, Pageable pageable);

	@EntityGraph(attributePaths = {"user", "applicationAnswers", "applicationAnswers.question",
		"applicationAnswers.question.checkLists"})
	@Query("SELECT a FROM Application a WHERE a.id IN " +
		"(SELECT aa.application.id FROM ApplicationAnswerCheckList aa " +
		"JOIN aa.checkList cl " +
		"JOIN aa.application.recruit r " +
		"WHERE r.id =:recruitId AND aa.question.id = :questionId AND cl.id IN :checkListIds) " +
		"ORDER BY a.id DESC")
	Page<Application> findAllByCheckList(
		@Param("recruitId") Long recruitId,
		@Param("questionId") Long questionId,
		@Param("checkListIds") List<Long> checkListIds,
		Pageable pageable);

	@Query("SELECT a FROM Application a "
		+ "JOIN FETCH a.user "
		+ "LEFT JOIN FETCH a.recruitStatus "
		+ "WHERE a.recruit.id = :recruitmentId "
		+ "ORDER BY a.id DESC")
	List<Application> findAllByRecruitmentIdWithUserAndRecruitStatusFetchJoinOrderByIdDesc(
		@Param("recruitmentId") Long recruitId);
}

