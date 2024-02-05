package com.gg.server.domain.match.utils;

import java.time.LocalDateTime;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.Season;
import com.gg.server.data.game.Team;
import com.gg.server.data.game.TeamUser;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.game.type.StatusType;
import com.gg.server.data.user.User;

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
