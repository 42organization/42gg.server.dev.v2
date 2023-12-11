package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
    List<TournamentGame> findAllByTournamentId(Long tournamentId);

    TournamentGame findByTournamentIdAndTournamentRound(Long id, TournamentRound tournamentRound);
}
