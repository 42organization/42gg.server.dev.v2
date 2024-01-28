package com.gg.server.domain.match.utils;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.exception.WinningTeamNotFoundException;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.user.data.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentGameTestUtils {

	/**
	 * 토너먼트에서 동일한 라운드의 경기들을 매칭 (생성)
	 * @param tournament 토너먼트
	 * @param round 해당 라운드와 동일한 라운드의 모든 경기를 매칭
	 *              ex ) 8강의 경우 8강의 4경기를 매칭
	 * @return 매칭된 토너먼트 게임
	 */
	public static List<TournamentGame> matchTournamentGames(Tournament tournament, TournamentRound round, Season season) {
		List<TournamentRound> sameRounds = TournamentRound.getSameRounds(round);
		List<TournamentGame> sameRoundGames = tournament.getTournamentGames().stream()
			.filter(o -> sameRounds.contains(o.getTournamentRound()))
			.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
			.collect(Collectors.toList());
		List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(tournament.getTournamentGames(),
			TournamentRound.getPreviousRoundNumber(round));

		for (int i = 0; i < round.getRoundNumber() / 2; ++i) {
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

	public static Team getWinningTeam(Game game) {
		return game.getTeams().stream()
			.filter(team -> Boolean.TRUE.equals(team.getWin()))
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

	private static List<TournamentGame> findSameRoundGames(List<TournamentGame> tournamentGames, int roundNum) {
		return tournamentGames.stream()
			.filter(tournamentGame -> roundNum == tournamentGame.getTournamentRound().getRoundNumber())
			.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
			.collect(Collectors.toList());
	}
}
