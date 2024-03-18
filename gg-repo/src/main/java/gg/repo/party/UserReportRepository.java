package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.party.UserReport;
import gg.data.user.User;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
	public Optional<UserReport> findByReporterAndReportee(User reporter, User reportee);

	public List<UserReport> findByReporteeAndRoomId(User reportee, Long roomId);

	@Query(value = "SELECT ur FROM UserReport ur "
		+ "JOIN FETCH ur.reporter "
		+ "JOIN FETCH ur.reportee "
		+ "JOIN FETCH ur.room",
		countQuery = "SELECT count(ur) FROM UserReport ur")
	Page<UserReport> findAllWithFetchJoin(Pageable pageable);
}
