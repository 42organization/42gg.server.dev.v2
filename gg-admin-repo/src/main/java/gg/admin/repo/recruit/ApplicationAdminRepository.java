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

	/**
	 * id 조건에 일치하는 지원서 목록 반환
	 * @param recruitId
	 * @param pageable
	 * @return
	 */
	@EntityGraph(attributePaths = {"user", "applicationAnswers", "applicationAnswers.question",
		"applicationAnswers.question.checkLists"})
	Page<Application> findByRecruitIdAndIsDeletedFalseOrderByIdDesc(Long recruitId, Pageable pageable);

	/**
	 * id 조건 및 체크리스트 조건에 일치하는 지원서 목록 반환
	 * @param recruitId
	 * @param questionId
	 * @param checkListIds
	 * @param pageable
	 */
	@EntityGraph(attributePaths = {"user", "applicationAnswers", "applicationAnswers.question",
		"applicationAnswers.question.checkLists"})
	@Query("SELECT a FROM Application a WHERE a.isDeleted = false AND a.id IN "
		+ "(SELECT aa.application.id FROM ApplicationAnswerCheckList aa "
		+ "JOIN aa.checkList cl "
		+ "JOIN aa.application.recruit r "
		+ "WHERE r.id =:recruitId AND aa.question.id = :questionId AND cl.id IN :checkListIds) "
		+ "ORDER BY a.id DESC")
	Page<Application> findAllByCheckList(
		@Param("recruitId") Long recruitId,
		@Param("questionId") Long questionId,
		@Param("checkListIds") List<Long> checkListIds,
		Pageable pageable);

	@EntityGraph(attributePaths = {"user", "applicationAnswers", "applicationAnswers.question",
		"applicationAnswers.question.checkLists"})
	@Query("SELECT a FROM Application a WHERE a.isDeleted = false AND a.id IN "
		+ "(SELECT aa.application.id FROM ApplicationAnswerText aa "
		+ "JOIN aa.application.recruit r "
		+ "WHERE r.id =:recruitId AND aa.question.id = :questionId AND aa.answer LIKE CONCAT('%', :search, '%')) "
		+ "ORDER BY a.id DESC")
	Page<Application> findAllByContainSearch(
		@Param("recruitId") Long recruitId,
		@Param("questionId") Long questionId,
		@Param("search") String search,
		Pageable pageable);

	@Query("SELECT a FROM Application a "
		+ "JOIN FETCH a.user "
		+ "LEFT JOIN FETCH a.recruitStatus "
		+ "WHERE a.recruit.id = :recruitmentId "
		+ "ORDER BY a.id DESC")
	List<Application> findAllByRecruitmentIdWithUserAndRecruitStatusFetchJoinOrderByIdDesc(
		@Param("recruitmentId") Long recruitId);
}

