package gg.pingpong.repo.coin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.pingpong.data.store.CoinHistory;
import gg.pingpong.data.user.User;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {
	@Query("SELECT CASE WHEN COUNT(ch) > 0 THEN true ELSE false END FROM CoinHistory ch "
		+ "WHERE ch.user = :user "
		+ "AND ch.history = :history "
		+ "AND ch.createdAt >= :startOfDay "
		+ "AND ch.createdAt < :endOfDay")
	boolean existsUserAttendedCheckToday(@Param("user") User user, @Param("history") String history,
		@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

	Optional<CoinHistory> findFirstByOrderByIdDesc();

	List<CoinHistory> findAllByUserOrderByIdDesc(User user);

	Page<CoinHistory> findAllByUserOrderByIdDesc(User user, Pageable pageable);
}
