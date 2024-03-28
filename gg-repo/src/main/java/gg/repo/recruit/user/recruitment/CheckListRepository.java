package gg.repo.recruit.user.recruitment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.recruit.recruitment.CheckList;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {
	@Query("SELECT c FROM CheckList c WHERE c.question.id IN :questionIds")
	List<CheckList> findAllByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
