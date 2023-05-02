package com.gg.server.domain.noti;

import com.gg.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotiRepository extends JpaRepository<Noti, Integer> {
    List<Noti> findByUserAndIsCheckedFalse(User user);
}
