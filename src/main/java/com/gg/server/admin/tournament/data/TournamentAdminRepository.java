package com.gg.server.admin.tournament.data;

import com.gg.server.domain.tournament.data.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentAdminRepository extends JpaRepository<Tournament, Long> {
}
