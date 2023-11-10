package com.gg.server.admin.tournament.data;

import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.tournament.data.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentAdminRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByTitle(String title);

//    boolean existsByTitle(String title);
}
