package com.gg.server.domain.user;

import com.gg.server.domain.user.type.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIntraId(String intraId);
    User getUserByIntraId(String IntraId);
    Page<User> findByIntraIdContains(Pageable pageable, String intraId);
}
