package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.RoomReport;
import gg.data.user.User;

public interface RoomReportRepository extends JpaRepository<RoomReport, Long> {
	public List<RoomReport> findByRoomId(Long roomId);

	public Optional<RoomReport> findByReporterAndRoomId(User reporter, Long roomId);
}
