package com.gg.server.admin.user.data;

import com.gg.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAdminRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);
}
