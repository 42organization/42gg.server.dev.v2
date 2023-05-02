package com.gg.server.domain.noti;

import com.gg.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotiRepository extends JpaRepository<Noti, Long> {
    List<Noti> findByUser(User user);
    Noti findByIdAndUser(Long notiId, User user);
    List<Noti> findAllByUser(User user);
    List<Noti> findAllByUserOrderByIdDesc(User user);
    void deleteAllByUser(User user);
}
