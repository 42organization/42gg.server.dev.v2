package gg.pingpong.admin.repo.manage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.manage.Announcement;

public interface AnnouncementAdminRepository extends JpaRepository<Announcement, Long> {

	Optional<Announcement> findFirstByOrderByIdDesc();
}
