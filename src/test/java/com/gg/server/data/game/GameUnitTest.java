package com.gg.server.data.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@DisplayName("GameUnitTest")
class GameUnitTest {
	List<Team> mockTeams;
	List<Game> games;

	@BeforeEach
	void setUp() {
		mockTeams = IntStream.range(0, 10)
			.mapToObj(i -> mock(Team.class))
			.collect(Collectors.toCollection(ArrayList::new));
		games = IntStream.range(0, 10)
			.mapToObj(i -> new Game())
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Nested
	@DisplayName("AddTeam")
	class AddTeam {
		@Test
		@DisplayName("단일 Team 추가 성공")
		public void addSingleTeamSuccess() {
			//given
			Game game = games.get(0);
			Team mockTeam = mockTeams.get(0);

			//when
			game.addTeam(mockTeam);

			//then
			assertEquals(1, game.getTeams().size());
			assertEquals(mockTeam, game.getTeams().get(0));
		}

		@Test
		@DisplayName("두개 Team 추가 성공")
		void addMultiTeamSuccess() {
			//given
			Game game = games.get(0);

			//when
			game.addTeam(mockTeams.get(0));
			game.addTeam(mockTeams.get(1));

			//then
			assertEquals(2, game.getTeams().size());
			assertEquals(mockTeams.get(0), game.getTeams().get(0));
			assertEquals(mockTeams.get(1), game.getTeams().get(1));
		}

		@Test
		@DisplayName("두개 이상의 Team 추가 실패")
		void addExceedTeamFailed() {
			//given
			Game game = games.get(0);

			//when
			game.addTeam(mockTeams.get(0));
			game.addTeam(mockTeams.get(1));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> game.addTeam(mockTeams.get(2)));
			assertEquals(ErrorCode.TEAM_SIZE_EXCEED, businessException.getErrorCode());
			assertEquals(ErrorCode.TEAM_SIZE_EXCEED.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("동일한 Team 추가 실패")
		void duplicatedTeamAddFailed() {
			//given
			Game game = games.get(0);

			//when
			game.addTeam(mockTeams.get(0));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> game.addTeam(mockTeams.get(0)));
			assertEquals(ErrorCode.TEAM_DUPLICATION, businessException.getErrorCode());
			assertEquals(ErrorCode.TEAM_DUPLICATION.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Game game = games.get(0);

			//when
			Team team = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> game.addTeam(team));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}
	}
}
