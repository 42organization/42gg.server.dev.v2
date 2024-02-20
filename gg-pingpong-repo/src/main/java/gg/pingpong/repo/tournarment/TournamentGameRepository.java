package gg.pingpong.repo.tournarment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.game.TournamentGame;
import gg.pingpong.data.game.type.TournamentRound;

public interface TournamentGameRepository extends JpaRepository<TournamentGame, Long> {
	List<TournamentGame> findAllByTournamentId(Long tournamentId);

	Optional<TournamentGame> findByTournamentIdAndTournamentRound(Long id, TournamentRound tournamentRound);

	List<TournamentGame> findByTournamentIdAndTournamentRoundIn(Long id, List<TournamentRound> tournamentRounds);

	Optional<TournamentGame> findByGameId(Long gameId);
}
