package com.gg.server.domain.tournament.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
    List<TournamentGame> findAllByTournamentId(Long tournamentId);
}
