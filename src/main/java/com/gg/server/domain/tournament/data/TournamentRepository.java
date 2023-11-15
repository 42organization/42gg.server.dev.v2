package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findAllByStatus(TournamentStatus status);
}
