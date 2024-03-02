package gg.repo.party;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.user.User;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
	Optional<UserRoom> findByUserAndRoom(User user, Room room);
}
