package com.gg.server.domain.match.utils;

import java.time.LocalDateTime;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.data.User;

public class GameTestUtils {
	public static Game createGame(User user, User enemy, Season season, Mode mode) {
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
		Game game = new Game(season, StatusType.BEFORE, mode, startTime, startTime.plusMinutes(15));
		Team teamA = new Team(game, -1, false);
		Team teamB = new Team(game, -1, false);
		new TeamUser(teamA, user);
		new TeamUser(teamB, enemy);
		return game;
	}

	public static Game createNormalGame(User user, User enemy, Season season) {
		return createGame(user, enemy, season, Mode.NORMAL);
	}
}
