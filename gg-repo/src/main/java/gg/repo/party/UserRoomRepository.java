package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import io.lettuce.core.dynamic.annotation.Param;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
	List<UserRoom> findByUserId(Long userId);

	List<UserRoom> findByRoomId(Long userId);

	Optional<UserRoom> findByUserAndRoom(User user, Room room);

	@Query("SELECT ur.room FROM UserRoom ur WHERE ur.user.id = :userId AND ur.isExist = true "
		+ "AND ur.room.status = :status ORDER BY ur.room.dueDate ASC")
	List<Room> findFinishRoomsByUserId(@Param("userId") Long userId, @Param("status") RoomType status);

	Optional<UserRoom> findByUserIdAndRoomIdAndIsExistTrue(Long userId, Long roomId);
}
