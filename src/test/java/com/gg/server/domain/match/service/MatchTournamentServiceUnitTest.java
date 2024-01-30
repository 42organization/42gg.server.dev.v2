package com.gg.server.domain.match.service;

import static com.gg.server.domain.match.utils.TournamentGameTestUtils.*;
import static com.gg.server.domain.tournament.type.RoundNumber.*;
import static com.gg.server.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
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

import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.type.TournamentMatchStatus;
import com.gg.server.domain.match.utils.GameTestUtils;
import com.gg.server.domain.match.utils.TournamentTestUtils;
import com.gg.server.domain.match.utils.UserTestUtils;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.type.RoundNumber;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.utils.annotation.UnitTest;

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
	@DisplayName("8강 경기가 진행중인 토너먼트에서 다음 라운드의 매칭 필요 유무를 확인한다.")
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
			TournamentGame finalTournamentGame = new TournamentGame(finalGame, tournament, TournamentRound.THE_FINAL);
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
	@DisplayName("토너먼트의 게임 매칭")
	class MatchTournamentGameTest {
	}

	@Nested
	@DisplayName("이전 경기의 결과를 수정하고 결과에 따라 다음 매칭된 경기의 플레이어 수정")
	class UpdateMatchResultTest {
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
