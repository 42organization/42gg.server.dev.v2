package com.gg.server.domain.match.utils;

import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;

import java.time.LocalDateTime;
import java.util.List;

public class TournamentTestUtils {
	public static Tournament createTournament(TournamentType type) {
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endTime = startTime.plusHours(3);

		Tournament tournament = Tournament.builder()
			.title("title")
			.contents("contents")
			.startTime(startTime)
			.endTime(endTime)
			.type(type)
			.status(TournamentStatus.BEFORE)
			.build();
		return tournament;
	}

	/**
	 * 진행중인 토너먼트 생성(참가자 8명)
	 * @param type
	 * @return
	 */
	public static Tournament createLiveTournament(TournamentType type) {
		Tournament tournament = createTournament(type);
		tournament.updateStatus(TournamentStatus.LIVE);
		List<TournamentUser> tournamentUsers = tournament.getTournamentUsers();
		LocalDateTime registerTime = tournament.getStartTime();
		for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER; ++i) {
			new TournamentUser(UserTestUtils.createUser(), tournament, true, registerTime);
		}
		return tournament;
	}
}
