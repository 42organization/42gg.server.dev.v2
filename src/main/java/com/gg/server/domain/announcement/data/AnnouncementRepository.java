package com.gg.server.domain.announcement.data;

import com.gg.server.domain.feedback.data.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findFirstByOrderByIdDesc();
}
