package gg.pingpong.api.data.game;

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

import gg.data.game.Team;
import gg.data.game.TeamUser;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

@UnitTest
@DisplayName("TeamUnitTest")
class TeamUnitTest {

	List<TeamUser> mockTeamUsers;
	List<Team> teams;

	@BeforeEach
	void setUp() {
		mockTeamUsers = IntStream.range(0, 10)
			.mapToObj(i -> mock(TeamUser.class))
			.collect(Collectors.toCollection(ArrayList::new));
		teams = IntStream.range(0, 10)
			.mapToObj(i -> new Team())
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Nested
	@DisplayName("AddTeamUser")
	class AddTeam {

		@Test
		@DisplayName("단일 TeamUser 추가 성공")
		public void addSingleTeamSuccess() {
			//given
			Team team = teams.get(0);
			TeamUser mockTeamUser = mockTeamUsers.get(0);

			//when
			team.addTeamUser(mockTeamUser);

			//then
			assertEquals(1, team.getTeamUsers().size());
			assertEquals(mockTeamUser, team.getTeamUsers().get(0));
		}

		@Test
		@DisplayName("두개 TeamUser 추가 성공")
		void addMultiTeamSuccess() {
			//given
			Team team = teams.get(0);

			//when
			team.addTeamUser(mockTeamUsers.get(0));
			team.addTeamUser(mockTeamUsers.get(1));

			//then
			assertEquals(2, team.getTeamUsers().size());
			assertEquals(mockTeamUsers.get(0), team.getTeamUsers().get(0));
			assertEquals(mockTeamUsers.get(1), team.getTeamUsers().get(1));
		}

		@Test
		@DisplayName("두개 이상의 TeamUser 추가 실패")
		void addExceedTeamFailed() {
			//given
			Team team = teams.get(0);

			//when
			team.addTeamUser(mockTeamUsers.get(0));
			team.addTeamUser(mockTeamUsers.get(1));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> team.addTeamUser(mockTeamUsers.get(2)));
			assertEquals(ErrorCode.TEAM_USER_EXCEED, businessException.getErrorCode());
			assertEquals(ErrorCode.TEAM_USER_EXCEED.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("동일한 TeamUser 추가 실패")
		void duplicatedTeamAddFailed() {
			//given
			Team team = teams.get(0);

			//when
			team.addTeamUser(mockTeamUsers.get(0));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> team.addTeamUser(mockTeamUsers.get(0)));
			assertEquals(ErrorCode.TEAM_USER_ALREADY_EXIST, businessException.getErrorCode());
			assertEquals(ErrorCode.TEAM_USER_ALREADY_EXIST.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Team team = teams.get(0);

			//when
			TeamUser teamUser = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> team.addTeamUser(teamUser));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}
	}
}
