package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.party.Room;
import gg.data.party.UserReport;
import gg.data.user.User;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
	Optional<UserReport> findByReporterAndReporteeAndRoom(User reporter, User reportee, Room room);

	@Query("SELECT ur FROM UserReport ur JOIN FETCH ur.reportee "
		+ "JOIN FETCH ur.room WHERE ur.reportee = :reportee AND ur.room.id = :roomId")
	List<UserReport> findByReporteeAndRoomId(@Param("reportee") User reportee, @Param("roomId") Long roomId);

	@Query(value = "SELECT ur FROM UserReport ur "
		+ "JOIN FETCH ur.reporter "
		+ "JOIN FETCH ur.reportee "
		+ "JOIN FETCH ur.room",
		countQuery = "SELECT count(ur) FROM UserReport ur")
	Page<UserReport> findAllWithUserReportFetchJoin(Pageable pageable);
}
