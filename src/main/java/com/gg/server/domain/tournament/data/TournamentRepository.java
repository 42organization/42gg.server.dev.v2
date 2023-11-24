package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findAllByStatus(TournamentStatus status);

    List<Tournament> findAllByStatusIsNot(TournamentStatus status);
}
