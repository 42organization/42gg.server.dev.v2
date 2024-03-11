package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.UserReport;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
