package gg.pingpong.api.user.match.service;

import static gg.pingpong.api.user.match.utils.TournamentGameTestUtils.*;
import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.manage.SlotManagement;
import gg.data.pingpong.match.type.TournamentMatchStatus;
import gg.data.pingpong.season.Season;
import gg.data.pingpong.tournament.Tournament;
import gg.data.pingpong.tournament.TournamentGame;
import gg.data.pingpong.tournament.type.RoundNumber;
import gg.data.pingpong.tournament.type.TournamentRound;
import gg.data.pingpong.tournament.type.TournamentStatus;
import gg.data.user.User;
import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import gg.pingpong.api.user.match.utils.GameTestUtils;
import gg.pingpong.api.user.match.utils.TournamentTestUtils;
import gg.pingpong.api.user.match.utils.UserTestUtils;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.repo.game.GameRepository;
import gg.repo.manage.SlotManagementRepository;
import gg.repo.tournarment.TournamentGameRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.match.EnrolledSlotException;
import gg.utils.exception.match.WinningTeamNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class MatchTournamentServiceUnitTest {
	@InjectMocks
	private MatchTournamentService matchTournamentService;
	@Mock
	private TournamentGameRepository tournamentGameRepository;
	@Mock
	private GameRepository gameRepository;
	@Mock
	private SlotManagementRepository slotManagementRepository;
	@Mock
	private SeasonFindService seasonFindService;
	@Mock
	private NotiAdminService notiAdminService;
	private static final Season season = Season.builder().startTime(LocalDateTime.now()).startPpp(123).build();
	private static Long gameId;
	private static Long tournamentGameId;

	@BeforeEach
	void init() {
		gameId = 1L;
		tournamentGameId = 1L;
	}

	@Nested
	@DisplayName("checkTournamentGame() - 8강 경기가 진행중인 토너먼트에서 다음 라운드의 매칭 필요 유무를 확인한다.")
	class CheckNextRoundTest {
		private Tournament tournament;

		@BeforeEach
		void init() {
			tournament = TournamentTestUtils.createTournament(TournamentStatus.LIVE);
			setFieldWithReflection(tournament, "id", 1L);
			for (TournamentGame tournamentGame : tournament.getTournamentGames()) {
				setFieldWithReflection(tournamentGame, "id", tournamentGameId++);
			}
			matchTournamentGames(tournament, QUARTER_FINAL, season);
			List<Game> games = tournament.getTournamentGames().stream()
				.filter(tournamentGame -> tournamentGame.getTournamentRound().getRoundNumber() == QUARTER_FINAL)
				.map(TournamentGame::getGame).collect(Collectors.toList());
			setGameIds(games);
		}

		@Test
		@DisplayName("8강의 모든 게임이 종료된 경우, 4강이 매칭 필요하므로 REQUIRED 반환")
		void checkRequiredMatch() {
			// given
			List<TournamentGame> quarterGames = getTournamentGamesByRoundNum(tournament, QUARTER_FINAL);
			finishTournamentGames(quarterGames);
			Random random = new Random();
			TournamentGame target = quarterGames.get(random.nextInt(quarterGames.size()));
			RoundNumber nextRound = target.getTournamentRound().getNextRound().getRoundNumber();
			given(tournamentGameRepository.findByGameId(target.getGame().getId())).willReturn(Optional.of(target));
			given(tournamentGameRepository.findAllByTournamentId(tournament.getId()))
				.willReturn(tournament.getTournamentGames());
			given(tournamentGameRepository.findByTournamentIdAndTournamentRoundIn(
				tournament.getId(), TournamentRound.getSameRounds(nextRound)))
				.willReturn(getTournamentGamesByRoundNum(tournament, nextRound));

			// when
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(target.getGame());

			// then
			assertThat(tournamentMatchStatus).isEqualTo(TournamentMatchStatus.REQUIRED);
		}

		@Test
		@DisplayName("8강에 종료되지 않은 게임이 존재할 경우, 4강 매칭이 불필요하므로 UNNECESSARY 반환")
		void checkUnnecessaryMatch() {
			// given
			TournamentGame quarterGame = getTournamentGameByRound(tournament, TournamentRound.QUARTER_FINAL_1).get();
			given(tournamentGameRepository.findByGameId(quarterGame.getGame().getId())).willReturn(
				Optional.of(quarterGame));
			given(tournamentGameRepository.findAllByTournamentId(tournament.getId()))
				.willReturn(tournament.getTournamentGames());

			// when
			TournamentMatchStatus matchStatus = matchTournamentService.checkTournamentGame(quarterGame.getGame());

			// then
			assertThat(matchStatus).isEqualTo(TournamentMatchStatus.UNNECESSARY);
		}

		@Test
		@DisplayName("결승전 게임일 경우, 매칭할 경기가 없으므로 NO_MORE_MATCHES을 반환하고 토너먼트는 종료된다.")
		void checkFinalGame() {
			// given
			User user = UserTestUtils.createUser();
			User enemy = UserTestUtils.createUser();
			Game finalGame = GameTestUtils.createGame(user, enemy, season, Mode.TOURNAMENT);
			TournamentGame finalTournamentGame = getTournamentGameByRound(tournament, TournamentRound.THE_FINAL).get();
			finalTournamentGame.updateGame(finalGame);
			finishTournamentGame(finalTournamentGame);
			given(tournamentGameRepository.findByGameId(finalGame.getId())).willReturn(
				Optional.of(finalTournamentGame));

			// when
			TournamentMatchStatus matchStatus = matchTournamentService.checkTournamentGame(finalGame);

			// then
			assertThat(matchStatus).isEqualTo(TournamentMatchStatus.NO_MORE_MATCHES);
			assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.END);
			assertThat(tournament.getWinner()).isNotNull();
		}

		@Test
		@DisplayName("다음 라운드인 4강 경기가 이미 매칭된 경우, ALREADY_MATCHED를 반환한다.")
		void checkAlreadyMatched() {
			// given
			finishTournamentGames(getTournamentGamesByRoundNum(tournament, QUARTER_FINAL));
			matchTournamentGames(tournament, SEMI_FINAL, season);
			Game targetGame = getTournamentGameByRound(tournament, TournamentRound.QUARTER_FINAL_1).get().getGame();
			given(tournamentGameRepository.findByGameId(targetGame.getId())).willReturn(Optional.of(
				getTournamentGameByRound(tournament, TournamentRound.QUARTER_FINAL_1).get()));
			given(tournamentGameRepository.findAllByTournamentId(tournament.getId()))
				.willReturn(tournament.getTournamentGames());
			given(tournamentGameRepository.findByTournamentIdAndTournamentRoundIn(
				tournament.getId(), TournamentRound.getSameRounds(SEMI_FINAL)))
				.willReturn(getTournamentGamesByRoundNum(tournament, SEMI_FINAL));

			// when
			TournamentMatchStatus matchStatus = matchTournamentService.checkTournamentGame(targetGame);

			// then
			assertThat(matchStatus).isEqualTo(TournamentMatchStatus.ALREADY_MATCHED);
		}
	}

	@Nested
	@DisplayName("matchGames() - 토너먼트의 게임 매칭")
	class MatchTournamentGameTest {
		private Tournament tournament;
		private SlotManagement slotManagement;

		@BeforeEach
		void init() {
			tournament = TournamentTestUtils.createTournament(TournamentStatus.LIVE);
			setFieldWithReflection(tournament, "id", 1L);
			for (TournamentGame tournamentGame : tournament.getTournamentGames()) {
				setFieldWithReflection(tournamentGame, "id", tournamentGameId++);
			}
			matchTournamentGames(tournament, QUARTER_FINAL, season);
			List<Game> games = tournament.getTournamentGames().stream()
				.filter(tournamentGame -> tournamentGame.getTournamentRound().getRoundNumber() == QUARTER_FINAL)
				.map(TournamentGame::getGame).collect(Collectors.toList());
			setGameIds(games);

			slotManagement = SlotManagement.builder()
				.startTime(tournament.getStartTime())
				.gameInterval(10)
				.build();
		}

		@Test
		@DisplayName("이미 매칭된 8강을 매칭하려고 할 때, EnrolledSlotException 발생")
		void matchAlreadyMatchedGame() {
			// given
			given(tournamentGameRepository.findByTournamentIdAndTournamentRoundIn(
				tournament.getId(), TournamentRound.getSameRounds(QUARTER_FINAL)))
				.willReturn(getTournamentGamesByRoundNum(tournament, QUARTER_FINAL));

			// when, then
			assertThatThrownBy(() -> matchTournamentService.matchGames(tournament, QUARTER_FINAL))
				.isInstanceOf(EnrolledSlotException.class);
		}

		@Test
		@DisplayName("4강 매칭 성공 후 noti 전송 및 게임 저장 확인")
		void matchSuccess() {
			// given
			List<TournamentGame> quarterGames = getTournamentGamesByRoundNum(tournament, QUARTER_FINAL);
			finishTournamentGames(quarterGames);
			given(seasonFindService.findCurrentSeason(tournament.getStartTime())).willReturn(season);
			given(slotManagementRepository.findCurrent(tournament.getStartTime())).willReturn(
				Optional.of(slotManagement));
			given(tournamentGameRepository.findAllByTournamentId(tournament.getId()))
				.willReturn(tournament.getTournamentGames());

			// when
			matchTournamentService.matchGames(tournament, SEMI_FINAL);

			// then
			verify(gameRepository, times(SEMI_FINAL.getRound() / 2)).save(any(Game.class));
			verify(notiAdminService, times(SEMI_FINAL.getRound())).sendAnnounceNotiToUser(
				any(SendNotiAdminRequestDto.class));
			List<TournamentGame> semiGames = getTournamentGamesByRoundNum(tournament, SEMI_FINAL);
			for (TournamentGame semiGame : semiGames) {
				assertThat(semiGame.getGame()).isNotNull();
			}
		}
	}

	@Nested
	@DisplayName("updateMatchedGameUser() - 이전 경기 8강의 수정된 결과에 따라 다음 매칭된 4강 경기의 플레이어 수정")
	class UpdateMatchResultTest {
		private Tournament tournament;

		@BeforeEach
		void init() {
			tournament = TournamentTestUtils.createTournament(TournamentStatus.LIVE);
			setFieldWithReflection(tournament, "id", 1L);
			for (TournamentGame tournamentGame : tournament.getTournamentGames()) {
				setFieldWithReflection(tournamentGame, "id", tournamentGameId++);
			}
			matchTournamentGames(tournament, QUARTER_FINAL, season);
			List<Game> games = tournament.getTournamentGames().stream()
				.filter(tournamentGame -> tournamentGame.getTournamentRound().getRoundNumber() == QUARTER_FINAL)
				.map(TournamentGame::getGame).collect(Collectors.toList());
			setGameIds(games);
		}

		@Test
		@DisplayName("이전 경기 8강의 결과가 없는 경우, WinningTeamNotFoundException 발생")
		void updateMatchResultWithoutWinner() {
			// given
			Game game = getTournamentGameByRound(tournament, TournamentRound.QUARTER_FINAL_1).get().getGame();

			// when, then
			Game nextMatchedGame = getTournamentGameByRound(tournament, TournamentRound.SEMI_FINAL_1).get().getGame();
			assertThatThrownBy(() -> matchTournamentService.updateMatchedGameUser(game, nextMatchedGame))
				.isInstanceOf(WinningTeamNotFoundException.class);
		}

		@Test
		@DisplayName("이미 매칭된 4강 경기의 플레이어를 성공적으로 업데이트")
		void success() {
			// given
			finishTournamentGames(getTournamentGamesByRoundNum(tournament, QUARTER_FINAL));
			matchTournamentGames(tournament, SEMI_FINAL, season);
			List<Game> semiGames = tournament.getTournamentGames().stream()
				.filter(tournamentGame -> tournamentGame.getTournamentRound().getRoundNumber() == SEMI_FINAL)
				.map(TournamentGame::getGame).collect(Collectors.toList());
			setGameIds(semiGames);
			Game modifiedGame = getTournamentGameByRound(tournament, TournamentRound.QUARTER_FINAL_1).get().getGame();
			Game nextMatchedGame = getTournamentGameByRound(tournament, TournamentRound.SEMI_FINAL_1).get().getGame();
			Team losingTeam = getWinningTeam(modifiedGame);
			Team winningTeam = getLosingTeam(modifiedGame);
			losingTeam.updateScore(1, false);
			winningTeam.updateScore(2, true);

			// when
			matchTournamentService.updateMatchedGameUser(modifiedGame, nextMatchedGame);

			// then
			List<User> nextGameUsers = new ArrayList<>();
			nextGameUsers.add(nextMatchedGame.getTeams().get(0).getTeamUsers().get(0).getUser());
			nextGameUsers.add(nextMatchedGame.getTeams().get(1).getTeamUsers().get(0).getUser());

			assertThat(nextGameUsers.contains(winningTeam.getTeamUsers().get(0).getUser())).isTrue();
			assertThat(nextGameUsers.contains(losingTeam.getTeamUsers().get(0).getUser())).isFalse();
			verify(notiAdminService, times(2)).sendAnnounceNotiToUser(
				any(SendNotiAdminRequestDto.class));
		}
	}

	private void finishTournamentGame(TournamentGame tournamentGame) {
		Game game = tournamentGame.getGame();
		setFieldWithReflection(game, "status", StatusType.END);
		List<Team> teams = game.getTeams();
		teams.get(0).updateScore(2, true);
		teams.get(1).updateScore(1, false);
	}

	private void finishTournamentGames(List<TournamentGame> tournamentGames) {
		for (TournamentGame tournamentGame : tournamentGames) {
			finishTournamentGame(tournamentGame);
		}
	}

	private void setGameId(Game game) {
		setFieldWithReflection(game, "id", gameId++);
	}

	private void setGameIds(List<Game> games) {
		for (Game game : games) {
			setGameId(game);
		}
	}
}
