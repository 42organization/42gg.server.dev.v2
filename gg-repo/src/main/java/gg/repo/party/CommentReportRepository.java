package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.CommentReport;
import gg.data.user.User;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
	public List<CommentReport> findByCommentId(Long commentId);

	public Optional<CommentReport> findByReporterAndCommentId(User reporter, Long commentId);
}

