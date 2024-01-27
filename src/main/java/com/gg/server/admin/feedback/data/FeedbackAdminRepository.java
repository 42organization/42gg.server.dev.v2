package com.gg.server.admin.feedback.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.domain.feedback.data.Feedback;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long>, FeedbackAdminRepositorySearch {
}
