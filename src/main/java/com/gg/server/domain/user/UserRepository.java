package com.gg.server.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);
    User getUserByIntraId(String IntraId);
    Page<User> findByIntraIdContains(Pageable pageable, String intraId);
}
