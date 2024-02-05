package com.gg.server.domain.announcement.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
	Optional<Announcement> findFirstByOrderByIdDesc();
}
