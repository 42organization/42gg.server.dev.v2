package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
    List<TournamentGame> findAllByTournamentId(Long tournamentId);

    Optional<TournamentGame> findByTournamentIdAndTournamentRound(Long id, TournamentRound tournamentRound);

    Optional<TournamentGame> findByGameId(Long gameId);
}
