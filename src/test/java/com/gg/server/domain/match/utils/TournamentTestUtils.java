package com.gg.server.domain.match.utils;

import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;

import java.time.LocalDateTime;

public class TournamentTestUtils {
	/**
	 * 테스트용 Tournament 객체를 생성한다. user와 tournamentGame도 함께 생성한다.
	 * @param status 테스트용 Tournament 객체의 status
	 * @return 테스트용 Tournament 객체
	 */
	public static Tournament createTournament(TournamentStatus status) {
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endTime = startTime.plusHours(3);
		Tournament tournament = Tournament.builder()
			.title("title")
			.contents("contents")
			.startTime(startTime)
			.endTime(endTime)
			.type(TournamentType.ROOKIE)
			.status(status)
			.build();
		createJoinedTournamentUsers(tournament);
		createTournamentGames(tournament);
		return tournament;
	}

	private static void createJoinedTournamentUsers(Tournament tournament) {
		LocalDateTime registerTime = tournament.getStartTime();
		for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER; ++i) {
			new TournamentUser(UserTestUtils.createUser(), tournament, true, registerTime);
		}
	}

	private static void createTournamentGames(Tournament tournament) {
		for (TournamentRound round : TournamentRound.values()) {
			new TournamentGame(null, tournament, round);
		}
	}
}
