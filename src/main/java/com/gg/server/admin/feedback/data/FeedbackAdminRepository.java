package com.gg.server.admin.feedback.data;

import com.gg.server.domain.feedback.data.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackAdminRepository extends JpaRepository<Feedback, Long> {
    @Query(value = "select f from Feedback f join fetch f.user " +
        "where f.user.intraId = :intraId order by f.user.intraId asc, f.createdAt asc")
    List<Feedback> findFeedbacksByUserIntraId(@Param("intraId") String intraId);
}
