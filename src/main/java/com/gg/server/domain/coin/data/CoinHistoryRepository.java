package com.gg.server.domain.coin.data;

import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {
    @Query("SELECT CASE WHEN COUNT(ch) > 0 THEN true ELSE false END FROM CoinHistory ch WHERE ch.user = :user AND ch.history = :history AND ch.createdAt >= :startOfDay AND ch.createdAt < :endOfDay")
    boolean existsUserAttendedCheckToday(@Param("user") User user, @Param("history") String history, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    Optional<CoinHistory> findFirstByOrderByIdDesc();

    List<CoinHistory> findAllByUserOrderByIdDesc(User user);

    Page<CoinHistory> findAllByUserOrderByIdDesc(User user, Pageable pageable);
}
