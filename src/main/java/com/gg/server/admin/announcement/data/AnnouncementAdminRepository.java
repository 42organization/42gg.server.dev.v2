package com.gg.server.admin.announcement.data;

import com.gg.server.domain.announcement.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementAdminRepository extends JpaRepository<Announcement, Long> {

    Announcement findFirstByOrderByIdDesc();
}
