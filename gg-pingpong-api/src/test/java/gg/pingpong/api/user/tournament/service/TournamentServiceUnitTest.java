package gg.pingpong.api.user.tournament.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import gg.pingpong.api.admin.noti.dto.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import gg.pingpong.api.user.match.service.MatchTournamentService;
import gg.pingpong.api.user.tournament.dto.TournamentGameListResponseDto;
import gg.pingpong.api.user.tournament.dto.TournamentResponseDto;
import gg.pingpong.api.user.tournament.dto.TournamentUserRegistrationResponseDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.api.utils.ReflectionUtilsForUnitTest;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Tournament;
import gg.pingpong.data.game.TournamentGame;
import gg.pingpong.data.game.TournamentUser;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.game.type.TournamentRound;
import gg.pingpong.data.game.type.TournamentStatus;
import gg.pingpong.data.game.type.TournamentType;
import gg.pingpong.data.game.type.TournamentUserStatus;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.data.user.type.SnsType;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.GameTeamUser;
import gg.pingpong.repo.tournarment.TournamentGameRepository;
import gg.pingpong.repo.tournarment.TournamentRepository;
import gg.pingpong.repo.tournarment.TournamentUserRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.tournament.TournamentConflictException;
import gg.pingpong.utils.exception.tournament.TournamentNotFoundException;
import gg.pingpong.utils.exception.user.UserNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
class TournamentServiceUnitTest {
	@Mock
	TournamentRepository tournamentRepository;
	@Mock
	TournamentGameRepository tournamentGameRepository;
	@Mock
	TournamentUserRepository tournamentUserRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	MatchTournamentService matchTournamentService;
	@Mock
	NotiAdminService notiAdminService;
	@Mock
	GameRepository gameRepository;
	@InjectMocks
	TournamentService tournamentService;

	@Nested
	@DisplayName("getAllTournamentList")
	class GetAllTournamentList {
		Pageable pageRequest;
		Page<Tournament> page;
		Page<TournamentResponseDto> responseDto;

		@BeforeEach
		void init() {
			pageRequest = PageRequest.of(1, 10);
			page = mock(Page.class);
			responseDto = mock(Page.class);

			when(page.map(any(Function.class))).thenReturn(responseDto);
			when(responseDto.getContent()).thenReturn(mock(List.class));
			when(responseDto.getTotalPages()).thenReturn(10);
		}

		@Test
		@DisplayName("type == null && status == null")
		void typeAndStatusNull() {
			//Arrange
			TournamentType type = null;
			TournamentStatus status = null;
			when(tournamentRepository.findAll(pageRequest)).thenReturn(page);

			//Act
			tournamentService.getAllTournamentList(pageRequest, type, status);

			//Assert
			verify(tournamentRepository, times(1)).findAll(pageRequest);
		}

		@Test
		@DisplayName("type == null")
		void typeNull() {
			//Arrange
			TournamentType type = null;
			TournamentStatus status = TournamentStatus.LIVE;
			when(tournamentRepository.findAllByStatus(status, pageRequest)).thenReturn(page);

			//Act
			tournamentService.getAllTournamentList(pageRequest, type, status);

			//Assert
			verify(tournamentRepository, times(1))
				.findAllByStatus(status, pageRequest);
		}

		@Test
		@DisplayName("status == null")
		void statusNull() {
			//Arrange
			TournamentType type = TournamentType.ROOKIE;
			TournamentStatus status = null;
			when(tournamentRepository.findAllByType(type, pageRequest)).thenReturn(page);

			//Act
			tournamentService.getAllTournamentList(pageRequest, type, status);

			//Assert
			verify(tournamentRepository, times(1))
				.findAllByType(type, pageRequest);
		}

		@Test
		@DisplayName("statusAndTypeNotNull")
		void statusAndTypeNotNull() {
			//Arrange
			TournamentType type = TournamentType.ROOKIE;
			TournamentStatus status = TournamentStatus.LIVE;
			when(tournamentRepository
				.findAllByTypeAndStatus(type, status, pageRequest))
				.thenReturn(page);

			//Act
			tournamentService.getAllTournamentList(pageRequest, type, status);

			//Assert
			verify(tournamentRepository, times(1))
				.findAllByTypeAndStatus(type, status, pageRequest);
		}

	}

	@Nested
	@DisplayName("getTournament")
	class GetTournament {
		@Test
		@DisplayName("success")
		void success() {
			//Arrange
			Long id = 1L;
			Tournament tournament = mock(Tournament.class);
			when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

			//Act
			TournamentResponseDto dto = tournamentService.getTournament(id);

			//Assert
			verify(tournamentRepository, times(1)).findById(id);
			Assertions.assertThat(dto).isNotNull();
		}

		@Test
		@DisplayName("TournamentNotFound")
		void tournamentNotFound() {
			//Arrange
			Long id = 1L;
			when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

			//Act, Assert
			assertThatThrownBy(() -> tournamentService.getTournament(id))
				.isInstanceOf(TournamentNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("getUserStatusInTournament")
	class GetUserStatusInTournament {
		Tournament tournament;
		TournamentUser tournamentUser;
		UserDto requestDto;

		@BeforeEach
		void init() {
			tournament = mock(Tournament.class);
			tournamentUser = mock(TournamentUser.class);
			requestDto = mock(UserDto.class);
		}

		@Test
		@DisplayName("찾을_수_없는_토너먼트")
		void tournamentNotFound() {
			// given
			Long tournamentId = 1L;
			given(tournamentRepository.findById(tournamentId)).willReturn(Optional.empty());

			// when, then
			assertThatThrownBy(() -> tournamentService
				.getUserStatusInTournament(tournamentId, requestDto))
				.isInstanceOf(TournamentNotFoundException.class);
		}

		@Test
		@DisplayName("토너먼트 참가중인 유저")
		void player() {
			// given
			given(tournamentRepository.findById(tournament.getId()))
				.willReturn(Optional.of(tournament));

			given(tournament.findTournamentUserByUserId(requestDto.getId()))
				.willReturn(Optional.of(tournamentUser));

			given(tournamentUser.getIsJoined())
				.willReturn(true);

			// when
			TournamentUserRegistrationResponseDto responseDto =
				tournamentService.getUserStatusInTournament(tournament.getId(), requestDto);

			// then
			Assertions.assertThat(responseDto.getStatus()).isEqualTo(TournamentUserStatus.PLAYER);
		}

		@Test
		@DisplayName("토너먼트 대기중인 유저")
		void waitPlayer() {
			// given
			given(tournamentRepository.findById(tournament.getId()))
				.willReturn(Optional.of(tournament));

			given(tournament.findTournamentUserByUserId(requestDto.getId()))
				.willReturn(Optional.of(tournamentUser));

			given(tournamentUser.getIsJoined())
				.willReturn(false);

			// when
			TournamentUserRegistrationResponseDto responseDto =
				tournamentService.getUserStatusInTournament(tournament.getId(), requestDto);

			// then
			Assertions.assertThat(responseDto.getStatus()).isEqualTo(TournamentUserStatus.WAIT);
		}

		@Test
		@DisplayName("토너먼트 신청하지 않은 유저")
		void beforePlayer() {
			// given
			given(tournamentRepository.findById(tournament.getId()))
				.willReturn(Optional.of(tournament));

			given(tournament.findTournamentUserByUserId(requestDto.getId()))
				.willReturn(Optional.empty());

			// when
			TournamentUserRegistrationResponseDto responseDto =
				tournamentService.getUserStatusInTournament(tournament.getId(), requestDto);

			// then
			Assertions.assertThat(responseDto.getStatus()).isEqualTo(TournamentUserStatus.BEFORE);
		}
	}

	@Nested
	@DisplayName("토너먼트_유저_신청_테스트")
	class RegisterTournamentUserTest {
		@Test
		@DisplayName("유저_상태_추가_성공")
		void success() {
			// given
			Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
				LocalDateTime.now(), LocalDateTime.now().plusHours(2));
			User user = createUser("testUser");
			List<TournamentUser> tournamentUserList = new ArrayList<>();
			given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
			given(tournamentUserRepository.findAllByUser(any(User.class))).willReturn(tournamentUserList);

			// when, then
			TournamentUserRegistrationResponseDto responseDto =
				tournamentService.registerTournamentUser(tournament.getId(), UserDto.from(user));
		}

		@Test
		@DisplayName("찾을_수_없는_토너먼트")
		void tournamentNotFound() {
			// given
			User user = createUser("testUser");
			given(tournamentRepository.findById(any(Long.class))).willReturn(Optional.empty());

			// when, then
			assertThatThrownBy(() -> tournamentService.registerTournamentUser(1L, UserDto.from(user)))
				.isInstanceOf(TournamentNotFoundException.class);
		}

		@Test
		@DisplayName("db에_없는_유저")
		void userNotFound() {
			// given
			Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
				LocalDateTime.now(), LocalDateTime.now().plusHours(2));
			User user = createUser("testUser");
			given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));
			given(userRepository.findById(null)).willReturn(Optional.empty());

			// when, then
			assertThatThrownBy(() -> tournamentService.registerTournamentUser(tournament.getId(), UserDto.from(user)))
				.isInstanceOf(UserNotFoundException.class);
		}

		@Test
		@DisplayName("이미_신청한_토너먼트_존재")
		void conflictedRegistration() {
			// given
			Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
				LocalDateTime.now(), LocalDateTime.now().plusHours(2));
			User user = createUser("testUser");
			List<TournamentUser> tournamentUserList = new ArrayList<>();
			tournamentUserList.add(new TournamentUser(user, tournament, true, LocalDateTime.now()));
			given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
			given(tournamentUserRepository.findAllByUser(any(User.class))).willReturn(tournamentUserList);

			// when, then
			assertThatThrownBy(() -> tournamentService.registerTournamentUser(tournament.getId(), UserDto.from(user)))
				.isInstanceOf(TournamentConflictException.class);
		}
	}

	@Nested
	@DisplayName("토너먼트_유저_참가_취소_테스트")
	class CancelTournamentUserRegistration {
		@Test
		@DisplayName("유저_참가_취소_성공")
		void success() {
			// given
			Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
				LocalDateTime.now(), LocalDateTime.now().plusHours(2));
			User user = createUser("testUser");
			ReflectionUtilsForUnitTest.setFieldWithReflection(user, "id", 1L);
			TournamentUser tournamentUser = new TournamentUser(user, tournament, true, LocalDateTime.now());
			given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));

			// when, then
			tournamentService.cancelTournamentUserRegistration(tournament.getId(), UserDto.from(user));
		}

		@Test
		@DisplayName("찾을_수_없는_토너먼트")
		void tournamentNotFound() {
			// given
			Long tournamentId = 1L;
			User user = createUser("testUser");
			given(tournamentRepository.findById(tournamentId)).willReturn(Optional.empty());

			// when, then
			assertThatThrownBy(
				() -> tournamentService.cancelTournamentUserRegistration(tournamentId, UserDto.from(user)))
				.isInstanceOf(TournamentNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("startTournament")
	class StartTournament {
		@Test
		@DisplayName("금일 진행하는 토너먼트 없음")
		void tournamentNotFound() {
			//Arrange
			List<Tournament> tournaments = new ArrayList<>();
			IntStream.range(0, 2).forEach((i) -> tournaments.add(i, mock(Tournament.class)));

			when(tournaments.get(0).getStartTime()).thenReturn(LocalDateTime.now().minusDays(1));
			when(tournaments.get(1).getStartTime()).thenReturn(LocalDateTime.now().plusDays(1));

			when(tournamentRepository.findAllByStatus((TournamentStatus.BEFORE))).thenReturn(tournaments);

			//Act
			tournamentService.startTournament();

			//Assert
			verify(tournaments.get(0), times(1)).getStartTime();
			verify(tournaments.get(1), times(1)).getStartTime();
			verify(tournaments.get(0), times(0)).updateStatus(TournamentStatus.LIVE);
			verify(tournaments.get(1), times(0)).updateStatus(TournamentStatus.LIVE);
			verify(notiAdminService, times(0)).sendAnnounceNotiToUser(any(SendNotiAdminRequestDto.class));
			verify(matchTournamentService, times(0)).matchGames(any(), any());
		}

		@Test
		@DisplayName("금일 진행하는 토너먼트 모두 인원수 미달")
		void everyTournamentNotFull() {
			//Arrange
			User user = mock(User.class);
			List<Tournament> tournaments = new ArrayList<>();
			List<TournamentUser> tournamentUsers = new ArrayList<>();
			IntStream.range(0, 2).forEach((i) -> tournaments.add(i, mock(Tournament.class)));
			IntStream.range(0, 7).forEach((i) -> tournamentUsers.add(i, mock(TournamentUser.class)));

			when(user.getIntraId()).thenReturn("testUser");
			when(tournaments.get(0).getStartTime()).thenReturn(LocalDateTime.now());
			when(tournaments.get(1).getStartTime()).thenReturn(LocalDateTime.now());
			when(tournamentRepository.findAllByStatus((TournamentStatus.BEFORE))).thenReturn(tournaments);

			tournaments.stream().forEach((t) -> when(t.getTournamentUsers()).thenReturn(tournamentUsers));

			tournamentUsers.stream().forEach((t) -> when(t.getIsJoined()).thenReturn(true));
			tournamentUsers.stream().forEach((t) -> when(t.getUser()).thenReturn(user));

			//Act
			tournamentService.startTournament();

			//Assert
			verify(tournaments.get(0), times(1))
				.getStartTime();
			verify(tournaments.get(1), times(1))
				.getStartTime();
			verify(tournaments.get(0), times(0))
				.updateStatus(TournamentStatus.LIVE);
			verify(tournaments.get(1), times(0))
				.updateStatus(TournamentStatus.LIVE);
			verify(notiAdminService, times(14))
				.sendAnnounceNotiToUser(any(SendNotiAdminRequestDto.class));
			verify(matchTournamentService, times(0))
				.matchGames(any(), any());
		}

		@Test
		@DisplayName("하나만 인원수 미달")
		void oneTournamentNotFull() {
			//Arrange
			User user = mock(User.class);
			List<Tournament> tournaments = new ArrayList<>();
			List<TournamentUser> notFullTournamentUsers = new ArrayList<>();
			List<TournamentUser> fullTournamentUsers = new ArrayList<>();
			IntStream.range(0, 2).forEach((i) -> tournaments.add(i, mock(Tournament.class)));
			IntStream.range(0, 7).forEach((i) -> notFullTournamentUsers.add(i, mock(TournamentUser.class)));
			IntStream.range(0, 16).forEach((i) -> fullTournamentUsers.add(i, mock(TournamentUser.class)));

			when(user.getIntraId()).thenReturn("testUser");
			when(tournaments.get(0).getStartTime()).thenReturn(LocalDateTime.now());
			when(tournaments.get(1).getStartTime()).thenReturn(LocalDateTime.now());
			when(tournamentRepository.findAllByStatus((TournamentStatus.BEFORE))).thenReturn(tournaments);

			when(tournaments.get(0).getTournamentUsers()).thenReturn(notFullTournamentUsers);
			when(tournaments.get(1).getTournamentUsers()).thenReturn(fullTournamentUsers);

			IntStream.range(0, 4).forEach((i) -> when(notFullTournamentUsers.get(i).getIsJoined()).thenReturn(true));
			IntStream.range(0, 4).forEach((i) -> when(notFullTournamentUsers.get(i).getUser()).thenReturn(user));
			IntStream.range(4, 7).forEach((i) -> when(notFullTournamentUsers.get(i).getIsJoined()).thenReturn(false));

			//Act
			tournamentService.startTournament();

			//Assert
			verify(tournaments.get(0), times(1))
				.getStartTime();
			verify(tournaments.get(1), times(1))
				.getStartTime();
			verify(tournaments.get(0), times(0))
				.updateStatus(TournamentStatus.LIVE);
			verify(tournaments.get(1), times(1))
				.updateStatus(TournamentStatus.LIVE);
			verify(notiAdminService, times(4))
				.sendAnnounceNotiToUser(any(SendNotiAdminRequestDto.class));
			verify(matchTournamentService, times(1))
				.matchGames(any(), any());
		}
	}

	@Nested
	@DisplayName("getTournamentGames")
	class GetTournamentGames {

		List<TournamentGame> existTournamentGames;

		void init() {
			existTournamentGames = new ArrayList<>();

			IntStream.range(0, 7)
				.forEach((i) -> existTournamentGames.add(i, mock(TournamentGame.class)));
			when(existTournamentGames.get(0).getTournamentRound()).thenReturn(
				TournamentRound.QUARTER_FINAL_4);
			when(existTournamentGames.get(1).getTournamentRound()).thenReturn(
				TournamentRound.QUARTER_FINAL_3);
			when(existTournamentGames.get(2).getTournamentRound()).thenReturn(
				TournamentRound.QUARTER_FINAL_2);
			when(existTournamentGames.get(3).getTournamentRound()).thenReturn(
				TournamentRound.QUARTER_FINAL_1);
			when(existTournamentGames.get(4).getTournamentRound()).thenReturn(
				TournamentRound.SEMI_FINAL_2);
			when(existTournamentGames.get(5).getTournamentRound()).thenReturn(
				TournamentRound.SEMI_FINAL_1);
			when(existTournamentGames.get(6).getTournamentRound()).thenReturn(
				TournamentRound.THE_FINAL);
		}

		@Test
		@DisplayName("토너먼트 게임 존재하지 않음")
		void tournamentGameNotExist() {
			// given
			Long tournamentId = 1L;
			List<TournamentGame> tournamentGames = new ArrayList<>();
			when(tournamentGameRepository.findAllByTournamentId(any(Long.class))).thenReturn(
				tournamentGames);

			// when
			TournamentGameListResponseDto result = tournamentService.getTournamentGames(
				tournamentId);

			// then
			Assertions.assertThat(result.getGames()).isEmpty();
			Assertions.assertThat(result.getTournamentId()).isEqualTo(tournamentId);
			verify(tournamentGameRepository, times(1)).findAllByTournamentId(any(Long.class));
			verify(gameRepository, times(0)).findTeamsByGameId(any(Long.class));
		}

		@Test
		@DisplayName("토너먼트 게임 존재하지만 게임 존재하지 않음")
		void tournamentGameExistGameNotExist() {
			// given
			init();
			Long tournamentId = 1L;
			when(tournamentGameRepository.findAllByTournamentId(any(Long.class))).thenReturn(
				existTournamentGames);

			// when
			TournamentGameListResponseDto result = tournamentService.getTournamentGames(
				tournamentId);

			// then
			Assertions.assertThat(result.getGames()).size().isEqualTo(7);
			Assertions.assertThat(result.getTournamentId()).isEqualTo(tournamentId);
			verify(tournamentGameRepository, times(1)).findAllByTournamentId(any(Long.class));
			verify(gameRepository, times(0)).findTeamsByGameId(any(Long.class));
		}

		@Test
		@DisplayName("토너먼트 게임, 게임 모두 존재")
		void tournamentGameAndGameExist() {
			// given
			init();
			Long tournamentId = 1L;

			GameTeamUser gameTeamUser = mock(GameTeamUser.class);
			when(tournamentGameRepository.findAllByTournamentId(any(Long.class))).thenReturn(
				existTournamentGames);
			when(gameRepository.findTeamsByGameId(any(Long.class))).thenReturn(
				Optional.of(gameTeamUser));
			when(gameTeamUser.getStatus()).thenReturn(StatusType.LIVE);

			IntStream.range(0, 7).forEach((i) -> {
				when(existTournamentGames.get(i).getGame()).thenReturn(mock(Game.class));
				when(existTournamentGames.get(i).getGame().getId()).thenReturn((long)i);
			});

			// when
			TournamentGameListResponseDto result = tournamentService.getTournamentGames(
				tournamentId);

			// then
			Assertions.assertThat(result.getGames()).size().isEqualTo(7);
			Assertions.assertThat(result.getTournamentId()).isEqualTo(tournamentId);
			verify(tournamentGameRepository, times(1)).findAllByTournamentId(any(Long.class));
			verify(gameRepository, times(7)).findTeamsByGameId(any(Long.class));
		}
	}

	private Tournament createTournament(Long id, TournamentStatus status, LocalDateTime startTime,
		LocalDateTime endTime) {
		Tournament tournament = Tournament.builder()
			.title(id + "st tournament")
			.contents("")
			.startTime(startTime)
			.endTime(endTime)
			.type(TournamentType.ROOKIE)
			.status(status)
			.build();
		ReflectionUtilsForUnitTest.setFieldWithReflection(tournament, "id", id);
		return tournament;
	}

	/**
	 * 유저 생성 매서드 - intraId로만 초기화
	 * @param intraId
	 * @return
	 */
	private User createUser(String intraId) {
		return User.builder()
			.eMail("email")
			.intraId(intraId)
			.racketType(RacketType.PENHOLDER)
			.snsNotiOpt(SnsType.NONE)
			.roleType(RoleType.USER)
			.totalExp(1000)
			.build();
	}
}
