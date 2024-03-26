package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.party.CommentReport;
import gg.data.user.User;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
	public List<CommentReport> findByCommentId(Long commentId);

	public Optional<CommentReport> findByReporterAndCommentId(User reporter, Long commentId);

	@Query(value = "SELECT cr FROM CommentReport cr "
		+ "JOIN FETCH cr.reporter "
		+ "JOIN FETCH cr.comment "
		+ "JOIN FETCH cr.room",
		countQuery = "SELECT count(cr) FROM CommentReport cr")
	Page<CommentReport> findAllWithCommentReportFetchJoin(Pageable pageable);
}
