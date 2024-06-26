package gg.repo.party;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.party.Room;
import gg.data.party.type.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {

	List<Room> findByStatus(RoomType status, Sort sort);

	@Query("SELECT r FROM Room r WHERE r.status IN :statuses")
	List<Room> findByStatusIn(@Param("statuses") List<RoomType> statuses);

	@Query("SELECT r FROM Room r WHERE r.status = :status AND r.startDate < :cutline")
	List<Room> findByStatusAndStartDate(@Param("status") RoomType status,
		@Param("cutline") LocalDateTime cutline);

}
