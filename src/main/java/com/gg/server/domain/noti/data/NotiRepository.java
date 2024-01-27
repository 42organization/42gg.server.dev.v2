package com.gg.server.domain.noti.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.domain.user.data.User;

public interface NotiRepository extends JpaRepository<Noti, Long>, NotiRepositoryCustom {
	List<Noti> findByUser(User user);

	Optional<Noti> findByIdAndUser(Long notiId, User user);

	List<Noti> findAllByUser(User user);

	List<Noti> findAllByUserOrderByIdDesc(User user);

	void deleteAllByUser(User user);
}
