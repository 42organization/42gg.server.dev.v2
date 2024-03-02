package gg.repo.party;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.UserRoom;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
	List<UserRoom> findByUserId(Long userId);
}
