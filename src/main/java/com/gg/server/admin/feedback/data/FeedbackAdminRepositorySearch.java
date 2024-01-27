package com.gg.server.admin.feedback.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.gg.server.domain.feedback.data.Feedback;

public interface FeedbackAdminRepositorySearch {
	Page<Feedback> findFeedbacksByUserIntraId(@Param("intraId") String intraId, Pageable pageable);
}
