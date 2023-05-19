package com.gg.server.admin.feedback.data;

import com.gg.server.domain.feedback.data.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long>, FeedbackAdminRepositorySearch {

}