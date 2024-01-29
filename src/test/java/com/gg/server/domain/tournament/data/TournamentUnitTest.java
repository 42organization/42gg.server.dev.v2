package com.gg.server.domain.tournament.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@DisplayName("TournamentUnitTest")
class TournamentUnitTest {

	List<TournamentGame> mockTournamentGames;
	List<TournamentUser> mockTournamentUsers;
	List<Tournament> tournaments;

	@BeforeEach
	void setUp() {
		mockTournamentGames = IntStream.range(0, 10)
			.mapToObj(i -> mock(TournamentGame.class))
			.collect(Collectors.toCollection(ArrayList::new));

		mockTournamentUsers = IntStream.range(0, 10)
			.mapToObj(i -> mock(TournamentUser.class))
			.collect(Collectors.toCollection(ArrayList::new));

		tournaments = IntStream.range(0, 10)
			.mapToObj(i -> new Tournament())
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Nested
	@DisplayName("AddTournamentGame")
	class AddTournamentGame {

		@Test
		@DisplayName("단일 TournamentGame 추가 성공")
		public void addSingleTournamentGameSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			TournamentGame mockTournamentGame = mockTournamentGames.get(0);

			//when
			tournament.addTournamentGame(mockTournamentGame);

			//then
			assertEquals(1, tournament.getTournamentGames().size());
			assertEquals(mockTournamentGame, tournament.getTournamentGames().get(0));
		}

		@Test
		@DisplayName("최대 TournamentGame 추가 성공")
		void addMultiTournamentGameSuccess() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			IntStream.range(0, 7).forEach(i -> tournament.addTournamentGame(mockTournamentGames.get(i)));

			//then
			assertEquals(7, tournament.getTournamentGames().size());
			IntStream.range(0, 7).forEach(i ->
				assertEquals(mockTournamentGames.get(i), tournament.getTournamentGames().get(i)));
		}

		@Test
		@DisplayName("최대 이상의 TournamentGame 추가 실패")
		void addExceedTournamentGameFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			IntStream.range(0, 8).forEach(i -> tournament.addTournamentGame(mockTournamentGames.get(i)));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.addTournamentGame(mockTournamentGames.get(7)));
			assertEquals(ErrorCode.TOURNAMENT_GAME_EXCEED, businessException.getErrorCode());
			assertEquals(ErrorCode.TOURNAMENT_GAME_EXCEED.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("동일한 TournamentGame 추가 실패")
		void duplicatedTournamentGameAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			tournament.addTournamentGame(mockTournamentGames.get(0));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.addTournamentGame(mockTournamentGames.get(0)));
			assertEquals(ErrorCode.TOURNAMENT_GAME_DUPLICATION, businessException.getErrorCode());
			assertEquals(ErrorCode.TOURNAMENT_GAME_DUPLICATION.getMessage(),
				businessException.getMessage());
		}

		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			TournamentGame tournamentGame = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.addTournamentGame(tournamentGame));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}
	}

	@Nested
	@DisplayName("AddTournamentUser")
	class AddTournamentUser {

		@Test
		@DisplayName("단일 TournamentUser 추가 성공")
		public void addSingleTournamentGameSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			TournamentUser mockTournamentUser = mockTournamentUsers.get(0);

			//when
			tournament.addTournamentUser(mockTournamentUser);

			//then
			assertEquals(1, tournament.getTournamentUsers().size());
			assertEquals(mockTournamentUser, tournament.getTournamentUsers().get(0));
		}

		@Test
		@DisplayName("여러 TournamentUser 추가 성공")
		void addMultiTournamentGameSuccess() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			IntStream.range(0, mockTournamentUsers.size()).forEach(i ->
				tournament.addTournamentUser(mockTournamentUsers.get(i)));

			//then
			assertEquals(mockTournamentUsers.size(), tournament.getTournamentUsers().size());
			IntStream.range(0, mockTournamentUsers.size()).forEach(i ->
				assertEquals(mockTournamentUsers.get(i), tournament.getTournamentUsers().get(i)));
		}

		@Test
		@DisplayName("동일한 TournamentUser 추가 실패")
		void duplicatedTournamentGameAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			tournament.addTournamentUser(mockTournamentUsers.get(0));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.addTournamentUser(mockTournamentUsers.get(0)));
			assertEquals(ErrorCode.TOURNAMENT_USER_DUPLICATION, businessException.getErrorCode());
			assertEquals(ErrorCode.TOURNAMENT_USER_DUPLICATION.getMessage(),
				businessException.getMessage());
		}

		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			TournamentUser tournamentUser = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.addTournamentUser(tournamentUser));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}
	}

	@Nested
	@DisplayName("DeleteTournamentUser")
	class DeleteTournamentUser {

		@Test
		@DisplayName("TournamentUser 삭제 성공")
		public void addSingleTournamentGameSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			TournamentUser mockTournamentUser = mockTournamentUsers.get(0);
			tournament.addTournamentUser(mockTournamentUser);

			//when
			tournament.deleteTournamentUser(mockTournamentUser);

			//then
			assertEquals(0, tournament.getTournamentUsers().size());
		}

		@Test
		@DisplayName("존재하지 않는 TournamentUser 삭제 실패")
		void duplicatedTournamentGameAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			tournament.addTournamentUser(mockTournamentUsers.get(0));

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.deleteTournamentUser(mockTournamentUsers.get(1)));
			assertEquals(ErrorCode.TOURNAMENT_USER_NOT_FOUND, businessException.getErrorCode());
			assertEquals(ErrorCode.TOURNAMENT_USER_NOT_FOUND.getMessage(),
				businessException.getMessage());
		}

		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			TournamentUser tournamentUser = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.deleteTournamentUser(tournamentUser));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}
	}

	@Nested
	@DisplayName("UpdateWinner")
	class UpdateWinner {
		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			User user = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.updateWinner(user));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("승자 업데이트 성공")
		void updateSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			User user = mock(User.class);

			//when
			tournament.updateWinner(user);

			//then
			assertEquals(user, tournament.getWinner());
		}
	}

	@Nested
	@DisplayName("UpdateStatus")
	class UpdateStatus {
		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			TournamentStatus status = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.updateStatus(status));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("상태 업데이트 성공")
		void updateSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			TournamentStatus status = TournamentStatus.LIVE;

			//when
			tournament.updateStatus(status);

			//then
			assertEquals(status, tournament.getStatus());
		}
	}

	@Nested
	@DisplayName("UpdateStatus")
	class UpdateEndTime {
		@Test
		@DisplayName("null 포인터 전달 시 실패")
		void nullAddFailed() {
			//given
			Tournament tournament = tournaments.get(0);

			//when
			LocalDateTime endTime = null;

			//then
			BusinessException businessException = assertThrows(BusinessException.class,
				() -> tournament.updateEndTime(endTime));
			assertEquals(ErrorCode.NULL_POINT, businessException.getErrorCode());
			assertEquals(ErrorCode.NULL_POINT.getMessage(), businessException.getMessage());
		}

		@Test
		@DisplayName("endTime 업데이트 성공")
		void updateEndTimeSuccess() {
			//given
			Tournament tournament = tournaments.get(0);
			LocalDateTime endTime = LocalDateTime.now().withMinute(0).withNano(0);

			//when
			tournament.updateEndTime(endTime);

			//then
			assertEquals(endTime, tournament.getEndTime());
		}
	}
}
