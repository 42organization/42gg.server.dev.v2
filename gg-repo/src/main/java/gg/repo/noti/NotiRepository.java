package gg.repo.noti;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.noti.Noti;
import gg.data.user.User;

public interface NotiRepository extends JpaRepository<Noti, Long>, NotiRepositoryCustom {
	List<Noti> findByUser(User user);

	Optional<Noti> findByIdAndUser(Long notiId, User user);

	List<Noti> findAllByUser(User user);

	List<Noti> findAllByUserOrderByIdDesc(User user);

	void deleteAllByUser(User user);
}
