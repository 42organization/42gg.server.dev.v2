package com.gg.server.domain.feedback.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	Feedback findFirstByOrderByIdDesc();
}
