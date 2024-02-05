package com.gg.server.data.game;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.gg.server.data.game.type.TournamentStatus;
import com.gg.server.data.game.type.TournamentType;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@DisplayName("TournamentGameUnitTest")
public class TournamentGameUnitTest {

	TournamentGame tournamentGame;

	@Nested
	@DisplayName("UpdateGame")
	class UpdateGame {
		@Test
		@DisplayName("TournamentGame의 게임 업데이트 성공")
		void updateSuccess() {
			//given
			tournamentGame = new TournamentGame(new Game(),
				new Tournament("", "", LocalDateTime.now(), LocalDateTime.now(),
					TournamentType.MASTER, TournamentStatus.BEFORE), null);
			Game game = Mockito.mock(Game.class);

			//when
			tournamentGame.updateGame(game);

			//then
			Assertions.assertEquals(game, tournamentGame.getGame());
		}
	}
}
