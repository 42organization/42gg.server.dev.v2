package com.gg.server.admin.feedback.data;

import com.gg.server.domain.feedback.data.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long>, FeedbackAdminRepositorySearch {
}