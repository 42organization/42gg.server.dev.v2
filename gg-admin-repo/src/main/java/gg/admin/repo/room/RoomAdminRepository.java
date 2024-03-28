package gg.admin.repo.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Category;
import gg.data.party.Room;

public interface RoomAdminRepository extends JpaRepository<Room, Long> {
	List<Room> findByCategory(Category category);

}
