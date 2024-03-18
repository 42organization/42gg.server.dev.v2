package gg.repo.recruit.user.recruitment;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.recruitment.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	@EntityGraph(attributePaths = {"checkList"})
	List<Question> findAllByRecruitId(Long recruitId);
}
