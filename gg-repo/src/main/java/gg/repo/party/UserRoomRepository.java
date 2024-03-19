package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
	List<UserRoom> findByUserId(Long userId);

	List<UserRoom> findByRoomId(Long roomId);

	@Query("SELECT ur.user FROM UserRoom ur WHERE ur.room.id = :roomId AND ur.isExist = true "
		+ "ORDER BY ur.modifiedAt ASC")
	List<User> findByIsExist(@Param("roomId") Long roomId);

	Optional<UserRoom> findByUserAndRoom(User user, Room room);

	@Query("SELECT ur.room FROM UserRoom ur WHERE ur.user.id = :userId AND ur.isExist = true "
		+ "AND ur.room.status = :status ORDER BY ur.room.dueDate ASC")
	List<Room> findFinishRoomsByUserId(@Param("userId") Long userId, @Param("status") RoomType status);

	@Query("SELECT ur.room FROM UserRoom ur WHERE ur.user.id = :userId "
		+ "AND ur.isExist = true AND ur.room.status = 'OPEN'")
	List<Room> findOpenRoomsByUserId(@Param("userId") Long userId);

	@Query("SELECT ur FROM UserRoom ur JOIN FETCH ur.user "
		+ "JOIN FETCH ur.room WHERE ur.user.id = :userId AND ur.room.id = :roomId AND ur.isExist = true")
	Optional<UserRoom> findByUserIdAndRoomIdAndIsExistTrue(@Param("userId") Long userId, @Param("roomId") Long roomId);
}
