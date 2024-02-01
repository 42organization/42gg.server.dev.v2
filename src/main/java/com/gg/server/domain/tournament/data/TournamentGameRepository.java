package com.gg.server.domain.tournament.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.game.TournamentGame;
import com.gg.server.data.game.type.TournamentRound;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
	List<TournamentGame> findAllByTournamentId(Long tournamentId);

	Optional<TournamentGame> findByTournamentIdAndTournamentRound(Long id, TournamentRound tournamentRound);

	Optional<TournamentGame> findByGameId(Long gameId);
}
