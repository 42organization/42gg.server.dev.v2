package com.gg.server.admin.user.data;

import com.gg.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRepository extends JpaRepository<User, Long> {
}
