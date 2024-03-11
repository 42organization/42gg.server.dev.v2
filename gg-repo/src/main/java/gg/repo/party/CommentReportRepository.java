package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
}

