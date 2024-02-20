package gg.pingpong.repo.announcement;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.manage.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
	Optional<Announcement> findFirstByOrderByIdDesc();
}
