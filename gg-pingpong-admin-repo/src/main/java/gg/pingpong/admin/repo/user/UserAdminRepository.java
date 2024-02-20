package gg.pingpong.admin.repo.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.user.User;

public interface UserAdminRepository extends JpaRepository<User, Long> {
	Optional<User> findByIntraId(String intraId);

	Page<User> findByIntraIdContains(Pageable pageable, String intraId);

	Page<User> findByIntraId(Pageable pageable, String intraId);

	Page<User> findAll(Pageable pageable);
}
