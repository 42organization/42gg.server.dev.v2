package gg.repo.party;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Room;
import gg.data.party.type.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {
	List<Room> findByStatus(RoomType status, Sort sort);
}
