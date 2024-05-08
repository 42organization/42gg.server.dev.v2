package gg.admin.repo.recruit.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.recruit.recruitment.Question;

public interface QuestionAdminRepository extends JpaRepository<Question, Long> {
}
