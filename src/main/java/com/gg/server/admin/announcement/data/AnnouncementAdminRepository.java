package com.gg.server.admin.announcement.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.domain.announcement.data.Announcement;

public interface AnnouncementAdminRepository extends JpaRepository<Announcement, Long> {

	Optional<Announcement> findFirstByOrderByIdDesc();
}
