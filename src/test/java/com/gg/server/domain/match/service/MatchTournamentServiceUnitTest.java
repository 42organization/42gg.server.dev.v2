package com.gg.server.domain.match.service;

import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.type.TournamentMatchStatus;
import com.gg.server.domain.match.utils.TournamentGameTestUtils;
import com.gg.server.domain.match.utils.TournamentTestUtils;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.utils.annotation.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gg.server.domain.tournament.type.RoundNumber.QUARTER_FINAL;
import static com.gg.server.utils.ReflectionUtilsForUnitTest.setFieldWithReflection;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

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
	private static final Season season  = Season.builder().startTime(LocalDateTime.now()).startPpp(123).build();

	@Nested
	@DisplayName("진행중인 토너먼트에서 다음 라운드의 게임 매칭이 가능한지 확인한다.")
	class CheckNextRoundTest {
		private Tournament tournament;

		@BeforeEach
		void init() {
			tournament = TournamentTestUtils.createLiveTournament(TournamentType.ROOKIE);
			setFieldWithReflection(tournament, "id", 1L);
			for (TournamentRound round : TournamentRound.values()) {
				new TournamentGame(null, tournament, round);
			}
			TournamentGameTestUtils.matchTournamentGames(tournament, QUARTER_FINAL, season);
		}

		@Test
		@DisplayName("8강의 모든 게임이 종료된 경우, 4강이 매칭 필요하므로 REQUIRED 반환")
		void checkPossibleMatch() {
			// given
			List<TournamentGame> quarterGames = tournament.getTournamentGames().stream()
				.filter(o -> o.getTournamentRound().getRoundNumber() == TournamentRound.QUARTER_FINAL_1.getRoundNumber())
				.collect(Collectors.toList());
//			List<TournamentGame> quarterGames = TournamentRound.getSameRounds()
			long id = 1L;
			for (TournamentGame quarterGame : quarterGames) {
				Game game = quarterGame.getGame();
				setFieldWithReflection(game, "status", StatusType.END);
				setFieldWithReflection(game, "id", id++);
			}
			TournamentGame checkTournamentGame = quarterGames.get(0);
			given(tournamentGameRepository.findByGameId(checkTournamentGame.getGame().getId()))
				.willReturn(Optional.of(checkTournamentGame));
			given(tournamentGameRepository.findAllByTournamentId(tournament.getId()))
				.willReturn(tournament.getTournamentGames());
			TournamentGame semiFinalGame = tournament.getTournamentGames().stream()
				.filter(o -> o.getTournamentRound().getRoundNumber() == TournamentRound.SEMI_FINAL_1.getRoundNumber())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("해당 라운드의 게임이 존재하지 않습니다."));
			given(tournamentGameRepository.findByTournamentIdAndTournamentRound(tournament.getId(), TournamentRound.SEMI_FINAL_1))
				.willReturn(Optional.of(semiFinalGame));

			// when
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(checkTournamentGame.getGame());

			// then
			assertThat(tournamentMatchStatus).isEqualTo(TournamentMatchStatus.REQUIRED);
		}

		@Test
		@DisplayName("8강에 종료되지 않은 게임이 존재할 경우, 4강 매칭이 불필요하므로 UNNECESSARY 반환")
		void checkImpossibleMatch() {
			// given
//			given(tournamentGameRepository.findByGameId(1L)).willReturn(null);

			// when
//			matchTournamentService.checkTournamentGame(game);
		}

		@Test
		@DisplayName("결승전 게임일 경우, 매칭할 경기가 없으므로 NO_MORE_MATCHES을 반환하고 토너먼트는 종료된다.")
		void checkFinalGame() {

		}

		@Test
		@DisplayName("이미 다음 라운드의 게임 매칭이 완료된 경우, ALREADY_MATCHED를 반환한다.")
		void checkAlreadyMatched() {
			// given
//			given(tournamentGameRepository.findByGameId(1L)).willReturn(null);
		}
	}

	@Nested
	@DisplayName("토너먼트의 게임 매칭")
	class  MatchTournamentGameTest {
	}

	@Nested
	@DisplayName("이전 경기의 결과를 수정하고 결과에 따라 다음 매칭된 경기의 플레이어 수정")
	class UpdateMatchResultTest {
	}

}
