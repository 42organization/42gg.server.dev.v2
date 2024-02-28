package gg.pingpong.api.user.match.dto;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import gg.data.game.Game;
import gg.data.game.type.Mode;
import gg.data.manage.SlotManagement;
import gg.utils.annotation.UnitTest;

@UnitTest
class MatchStatusDtoUnitTest {

	@Nested
	@DisplayName("MatchStatusDto(Game game, String userIntraId, String enemyIntraId, SlotManagement slotManagement)")
	class MatchStatusDtoConstructor {
		Game tournamentGame;
		Game rankGame;
		Game normalGame;
		SlotManagement slotManagement;
		String userIntraId = "myId";
		String enemyIntraId = "enemyId";
		LocalDateTime time = LocalDateTime.now();

		@BeforeEach
		void setup() {
			tournamentGame = mock(Game.class);
			rankGame = mock(Game.class);
			normalGame = mock(Game.class);
			slotManagement = mock(SlotManagement.class);
			when(slotManagement.getOpenMinute()).thenReturn(5);
		}

		@ParameterizedTest
		@DisplayName("Game Mode 가 토너먼트인 경우 항상 Imminent")
		@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
		void ifGameTypeTournamentAlwaysImminent(int arg) {
			//Arrange
			when(tournamentGame.getMode()).thenReturn(Mode.TOURNAMENT);
			when(tournamentGame.getStartTime()).thenReturn(time.plusMinutes(arg));

			//Act
			MatchStatusDto dto = new MatchStatusDto(tournamentGame, userIntraId, enemyIntraId, slotManagement);

			//Assert
			Assertions.assertThat(dto.getIsImminent()).isEqualTo(true);
		}

		@ParameterizedTest
		@DisplayName("Game Mode 가 토너먼트가 아닌 경우 시간에 따라 NotImminent")
		@ValueSource(ints = {6, 7, 8, 9, 10})
		void rankAndNormalGameNotImminent(int arg) {
			//Arrange
			when(rankGame.getMode()).thenReturn(Mode.RANK);
			when(normalGame.getMode()).thenReturn(Mode.RANK);
			when(rankGame.getStartTime()).thenReturn(time.plusMinutes(arg));
			when(normalGame.getStartTime()).thenReturn(time.plusMinutes(arg));

			//Act
			MatchStatusDto rankDto = new MatchStatusDto(rankGame, userIntraId, enemyIntraId, slotManagement);
			MatchStatusDto normalDto = new MatchStatusDto(normalGame, userIntraId, enemyIntraId, slotManagement);

			//Assert
			Assertions.assertThat(rankDto.getIsImminent())
				.isEqualTo(normalDto.getIsImminent())
				.isEqualTo(false);
		}

		@ParameterizedTest
		@DisplayName("Game Mode 가 토너먼트가 아닌 경우 시간에 따라 Imminent")
		@ValueSource(ints = {0, 1, 2, 3, 4, 5})
		void rankAndNormalGameImminent(int arg) {
			//Arrange
			when(rankGame.getMode()).thenReturn(Mode.RANK);
			when(normalGame.getMode()).thenReturn(Mode.RANK);
			when(rankGame.getStartTime()).thenReturn(time.plusMinutes(arg));
			when(normalGame.getStartTime()).thenReturn(time.plusMinutes(arg));

			//Act
			MatchStatusDto rankDto = new MatchStatusDto(rankGame, userIntraId, enemyIntraId, slotManagement);
			MatchStatusDto normalDto = new MatchStatusDto(normalGame, userIntraId, enemyIntraId, slotManagement);

			//Assert
			Assertions.assertThat(rankDto.getIsImminent())
				.isEqualTo(normalDto.getIsImminent())
				.isEqualTo(true);
		}
	}

}
