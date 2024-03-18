package gg.repo.recruit.user.recruitment;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.recruitment.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	@EntityGraph(attributePaths = {"checkLists"})
	@Query("SELECT q FROM Question q WHERE q.recruit.id = :recruitId ORDER BY q.sortNum")
	List<Question> findAllByRecruitId(@Param("recruitId") Long recruitId);
}
