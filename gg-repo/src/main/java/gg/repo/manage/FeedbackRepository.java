package gg.repo.manage;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.manage.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	Feedback findFirstByOrderByIdDesc();
}
