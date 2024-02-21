package gg.pingpong.api.user.match.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Team;
import gg.pingpong.data.game.TeamUser;
import gg.pingpong.data.game.Tournament;
import gg.pingpong.data.game.TournamentGame;
import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.game.type.RoundNumber;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.game.type.TournamentRound;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.exception.match.WinningTeamNotFoundException;

public class TournamentGameTestUtils {

	/**
	 * 토너먼트에서 동일한 라운드의 경기들을 매칭 (생성)
	 * @param tournament 토너먼트
	 * @param roundNumber 해당 라운드와 동일한 라운드의 모든 경기를 매칭
	 *              ex ) 8강의 경우 8강의 4경기를 매칭
	 * @return 매칭된 토너먼트 게임
	 */
	public static List<TournamentGame> matchTournamentGames(Tournament tournament, RoundNumber roundNumber,
		Season season) {
		List<TournamentRound> sameRounds = TournamentRound.getSameRounds(roundNumber);
		List<TournamentGame> sameRoundGames = tournament.getTournamentGames().stream()
			.filter(o -> sameRounds.contains(o.getTournamentRound()))
			.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
			.collect(Collectors.toList());
		RoundNumber previousRoundNumber = TournamentRound.getPreviousRoundNumber(roundNumber);
		List<TournamentGame> previousRoundTournamentGames = previousRoundNumber != null
			? findSameRoundGames(tournament.getTournamentGames(), previousRoundNumber)
			: new ArrayList<>();

		for (int i = 0; i < roundNumber.getRound() / 2; ++i) {
			Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
			Team team1 = new Team(game, -1, false);
			Team team2 = new Team(game, -1, false);
			User user1 = findMatchUser(previousRoundTournamentGames, i * 2, tournament);
			User user2 = findMatchUser(previousRoundTournamentGames, i * 2 + 1, tournament);
			new TeamUser(team1, user1);
			new TeamUser(team2, user2);
			sameRoundGames.get(i).updateGame(game);
		}
		return sameRoundGames;
	}

	/**
	 *
	 * @param tournament 토너먼트
	 * @param roundNumber 해당 라운드와 동일한 라운드를 찾는다.
	 * @return 해당 라운드의 모든 토너먼트 게임을 반환한다.
	 */
	public static List<TournamentGame> getTournamentGamesByRoundNum(Tournament tournament, RoundNumber roundNumber) {
		return tournament.getTournamentGames().stream()
			.filter(tournamentGame -> roundNumber == tournamentGame.getTournamentRound().getRoundNumber())
			.collect(Collectors.toList());
	}

	/**
	 *
	 * @param tournament 토너먼트
	 * @param round 해당 라운드와 동일한 라운드를 찾는다.
	 * @return 해당 라운드의 토너먼트 게임을 반환한다.
	 */
	public static Optional<TournamentGame> getTournamentGameByRound(Tournament tournament, TournamentRound round) {
		return tournament.getTournamentGames().stream()
			.filter(tournamentGame -> round == tournamentGame.getTournamentRound())
			.findFirst();
	}

	public static Team getWinningTeam(Game game) {
		return game.getTeams().stream()
			.filter(team -> Boolean.TRUE.equals(team.getWin()))
			.findAny()
			.orElseThrow(WinningTeamNotFoundException::new);
	}

	public static Team getLosingTeam(Game game) {
		return game.getTeams().stream()
			.filter(team -> Boolean.FALSE.equals(team.getWin()))
			.findAny()
			.orElseThrow(WinningTeamNotFoundException::new);
	}

	private static User findMatchUser(List<TournamentGame> previousTournamentGames, int index, Tournament tournament) {
		if (previousTournamentGames.isEmpty()) {
			return tournament.getTournamentUsers().get(index).getUser();
		}
		Game game = previousTournamentGames.get(index).getGame();
		return getWinningTeam(game)
			.getTeamUsers().get(0).getUser();
	}

	private static List<TournamentGame> findSameRoundGames(List<TournamentGame> tournamentGames, RoundNumber roundNum) {
		return tournamentGames.stream()
			.filter(tournamentGame -> roundNum == tournamentGame.getTournamentRound().getRoundNumber())
			.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
			.collect(Collectors.toList());
	}
}
