package com.gg.server.admin.announcement.data;

import com.gg.server.domain.announcement.data.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementAdminRepository extends JpaRepository<Announcement, Long> {

    Announcement findFirstByOrderByIdDesc();
}
