package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.UserReport;
import gg.data.user.User;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
	public Optional<UserReport> findByReporterAndReportee(User reporter, User reportee);

	public List<UserReport> findByReporteeAndRoomId(User reportee, Long roomId);
}
