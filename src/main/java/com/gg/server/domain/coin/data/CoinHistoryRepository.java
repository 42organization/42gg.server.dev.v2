package com.gg.server.domain.coin.data;

import com.gg.server.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {
    @Query("SELECT CASE WHEN COUNT(ch) > 0 THEN true ELSE false END FROM CoinHistory ch WHERE ch.user = :user AND ch.history = :history AND ch.createdAt >= :startOfDay AND ch.createdAt <= :endOfDay")
    boolean existsCoinHistoryByUserAndHistoryAndCreatedAtToday(User user, String history, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
