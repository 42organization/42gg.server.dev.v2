package gg.pingpong.api.user.match.utils;

import java.time.LocalDateTime;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.season.Season;
import gg.data.user.User;

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
