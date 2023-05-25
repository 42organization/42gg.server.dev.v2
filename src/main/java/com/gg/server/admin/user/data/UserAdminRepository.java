package com.gg.server.admin.user.data;

import com.gg.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAdminRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);
    User getUserByIntraId(String IntraId);
    Page<User> findByIntraIdContains(Pageable pageable, String intraId);
    Page<User> findByIntraId(Pageable pageable, String intraId);
    Optional<User> findByKakaoId(Long kakaoId);

    Page<User> findAll(Pageable pageable);
}
