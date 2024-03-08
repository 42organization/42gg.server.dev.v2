package gg.repo.party;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Room;
import gg.data.party.type.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {
	List<Room> findByStatus(RoomType status, Sort sort);

	Long findById(Long id, Sort sort);

	Page<Room> findAll(Pageable pageable);
}
