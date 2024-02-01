package com.gg.server.admin.feedback.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.Feedback;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long>, FeedbackAdminRepositorySearch {
}
