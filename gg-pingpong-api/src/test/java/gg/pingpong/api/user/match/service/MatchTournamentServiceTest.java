package gg.pingpong.api.user.match.service;

import static gg.pingpong.data.game.type.RoundNumber.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import gg.pingpong.api.user.match.utils.MatchIntegrationTestUtils;
import gg.pingpong.api.user.match.utils.TournamentGameTestUtils;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Team;
import gg.pingpong.data.game.Tournament;
import gg.pingpong.data.game.TournamentGame;
import gg.pingpong.data.game.type.RoundNumber;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.game.type.TournamentRound;
import gg.pingpong.data.game.type.TournamentStatus;
import gg.pingpong.data.match.type.TournamentMatchStatus;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.manage.SlotManagementRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.exception.match.EnrolledSlotException;
import gg.pingpong.utils.exception.match.SlotNotFoundException;
import gg.pingpong.utils.exception.match.WinningTeamNotFoundException;

@IntegrationTest
@Transactional
public class MatchTournamentServiceTest {
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	SlotManagementRepository slotManagementRepository;
	@Autowired
	GameRepository gameRepository;
	@Autowired
	MatchTournamentService matchTournamentService;
	@Autowired
	MatchIntegrationTestUtils matchTestUtils;
	@MockBean
	NotiAdminService notiAdminService;

	Tournament tournament;
	List<TournamentGame> allTournamentGames;

	@BeforeEach
	public void beforeEach() {
		testDataUtils.createSeason();
		testDataUtils.createSlotManagement(15);
		tournament = testDataUtils.createTournamentWithUser(Tournament.ALLOWED_JOINED_NUMBER, 4, "test");
		allTournamentGames = testDataUtils.createTournamentGameList(tournament, 7);
	}

	@Nested
	@DisplayName("토너먼트 라운드별 경기 생성 테스트")
	class MatchTournament {
		@Test
		@DisplayName("8강 경기 매칭 성공")
		public void quarterTest() {
			// when
			matchTournamentService.matchGames(tournament, RoundNumber.QUARTER_FINAL);

			// then
			List<TournamentRound> quarterRounds = TournamentRound.getSameRounds(RoundNumber.QUARTER_FINAL);
			List<TournamentGame> quarterRoundGames = allTournamentGames.stream()
				.filter(o -> quarterRounds.contains(o.getTournamentRound()))
				.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
				.collect(Collectors.toList());
			LocalDateTime startTime = tournament.getStartTime();
			int gameInterval = slotManagementRepository.findCurrent(startTime)
				.orElseThrow(SlotNotFoundException::new)
				.getGameInterval();

			// 4개의 8강 경기가 생성되었는지 확인
			assertThat(quarterRoundGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 2);
			for (TournamentGame tournamentGame : quarterRoundGames) {
				assertThat(tournamentGame.getGame()).isNotNull();
				assertThat(tournamentGame.getGame().getStatus()).isEqualTo(StatusType.BEFORE);
				assertThat(tournamentGame.getGame().getStartTime()).isEqualTo(startTime);
				assertThat(tournamentGame.getGame().getEndTime()).isEqualTo(startTime.plusMinutes(gameInterval));
				startTime = startTime.plusMinutes((long)gameInterval);
			}
			verify(notiAdminService, times(8)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
		}

		@Test
		@DisplayName("4강 경기 매칭 성공")
		public void semiTest() {
			// given
			// 8강 경기 결과
			List<TournamentGame> tournamentGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(tournamentGames, List.of(2, 0));

			// when
			matchTournamentService.matchGames(tournament, SEMI_FINAL);

			// then
			List<TournamentRound> semiRounds = TournamentRound.getSameRounds(SEMI_FINAL);
			List<TournamentGame> semiRoundGames = allTournamentGames.stream()
				.filter(o -> semiRounds.contains(o.getTournamentRound()))
				.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
				.collect(Collectors.toList());
			// 2개의 4강 경기가 생성되었는지 확인
			assertThat(semiRoundGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 4);
			for (TournamentGame tournamentGame : semiRoundGames) {
				assertThat(tournamentGame.getGame()).isNotNull();
				assertThat(tournamentGame.getGame().getStatus()).isEqualTo(StatusType.BEFORE);
			}
			// 8강에서 이긴 유저끼리 4강에 매칭되었는지 확인
			List<TournamentRound> quarterRounds = TournamentRound.getSameRounds(QUARTER_FINAL);
			List<TournamentGame> quarterRoundGames = allTournamentGames.stream()
				.filter(o -> quarterRounds.contains(o.getTournamentRound()))
				.sorted(Comparator.comparing(TournamentGame::getTournamentRound))
				.collect(Collectors.toList());
			List<User> semiTeams = new ArrayList<>();
			List<User> quarterWinningTeams = new ArrayList<>();
			for (TournamentGame semiRoundGame : semiRoundGames) {
				semiTeams.add(semiRoundGame.getGame().getTeams().get(0).getTeamUsers().get(0).getUser());
				semiTeams.add(semiRoundGame.getGame().getTeams().get(1).getTeamUsers().get(0).getUser());
			}
			for (TournamentGame quarterRoundGame : quarterRoundGames) {
				Team winningTeam = TournamentGameTestUtils.getWinningTeam(quarterRoundGame.getGame());
				quarterWinningTeams.add(winningTeam.getTeamUsers().get(0).getUser());
			}
			assertThat(semiTeams).contains(quarterWinningTeams.get(0));
			assertThat(semiTeams).contains(quarterWinningTeams.get(1));
			verify(notiAdminService, times(4)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
		}

		@Test
		@DisplayName("결승 경기 매칭 테스트 성공")
		public void finalTest() {
			// given
			// 8강 & 4강 경기 결과 입력
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(semiGames, List.of(2, 0));

			// when
			matchTournamentService.matchGames(tournament, THE_FINAL);

			// then
			// 1개의 결승 경기가 생성되었는지 확인
			TournamentGame finalRoundGame = allTournamentGames.stream()
				.filter(o -> TournamentRound.THE_FINAL.equals(o.getTournamentRound())).findAny().orElse(null);
			assertThat(finalRoundGame.getGame()).isNotNull();
			verify(notiAdminService, times(2)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
		}

		@Test
		@DisplayName("이미 매칭된 게임이 존재할 경우 실패")
		public void failAlreadyMatched() {
			// given
			// 8강 경기 매칭 + 4강 경기 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(semiGames, List.of(2, 0));

			// when, then
			assertThatThrownBy(() -> matchTournamentService.matchGames(tournament, SEMI_FINAL))
				.isInstanceOf(EnrolledSlotException.class);
		}
	}

	@Nested
	@DisplayName("토너먼트 매칭 가능 상태 확인 테스트")
	class CheckTournament {
		@Test
		@DisplayName("IMPOSSIBLE : 결승 경기 점수 입력 후 토너먼트 END 상태로 업데이트 성공 & 종료 시간 갱신")
		public void finalEndTest() {
			// given
			// 8강 & 4강 & 결승 경기 결과 입력
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(semiGames, List.of(2, 0));
			Game finalGame = matchTestUtils.matchTournamentGames(tournament, TournamentRound.THE_FINAL)
				.get(0)
				.getGame();
			matchTestUtils.updateTournamentGameResult(finalGame, List.of(2, 0));

			// when
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(finalGame);

			// then
			// 토너먼트 상태가 END로 변경되었는지
			// winner가 존재하는지 확인
			assertThat(TournamentMatchStatus.NO_MORE_MATCHES).isEqualTo(tournamentMatchStatus);
			assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.END);
			assertThat(tournament.getWinner()).isNotNull();
			assertThat(tournament.getEndTime()).isEqualTo(finalGame.getEndTime());
		}

		@Test
		@DisplayName("IMPOSSBLE : 진행중인 라운드의 모든 경기가 점수입력 완료되지 않을 경우")
		public void impossibleTest() {
			// given
			// 8강 경기 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);

			// when
			matchTestUtils.updateTournamentGameResult(quarterGames.get(0).getGame(), List.of(2, 0));
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(
				quarterGames.get(0).getGame());

			// then
			assertThat(tournamentMatchStatus).isEqualTo(TournamentMatchStatus.UNNECESSARY);
		}

		@Test
		@DisplayName("ALREADY_MATCHED : 이미 매칭된 게임이 존재할 경우")
		public void alreadyMatchedTest() {
			// given
			// 8강 경기 매칭 + 4강 경기 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(semiGames, List.of(2, 0));

			// when
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(
				quarterGames.get(0).getGame());

			// then
			assertThat(tournamentMatchStatus).isEqualTo(TournamentMatchStatus.ALREADY_MATCHED);
		}

		@Test
		@DisplayName("POSSIBLE : 토너먼트 매칭 가능 상태")
		public void possibleTest() {
			// given
			// 8강 경기 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));

			// when
			TournamentMatchStatus tournamentMatchStatus = matchTournamentService.checkTournamentGame(
				quarterGames.get(0).getGame());

			// then
			assertThat(tournamentMatchStatus).isEqualTo(TournamentMatchStatus.REQUIRED);
		}

	}

	@Nested
	@DisplayName("위너 변경에 따른 다음 경기 팀 변경 테스트")
	class ChangeTeam {
		@Test
		@DisplayName("8강 경기에서 4강 경기로 팀 변경 성공")
		public void quarterToSemiTest() {
			// given
			// 8강 경기 결과 + 4강 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);

			Game game = quarterGames.get(0)
				.getGame();                                              // QAUARTER_FINAL_1l Game
			TournamentRound nextRound = quarterGames.get(0).getTournamentRound().getNextRound();    // SEMI_FINAL_1
			Game nextMatchedGame = semiGames.stream()                                               // SEMI_FINAL_1 Game
				.filter(o -> nextRound.equals(o.getTournamentRound()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("다음 경기가 존재하지 않습니다.")).getGame();

			// when
			// 기존 8강 경기에서 진 팀이 점수 수정으로 이긴 팀으로 변경
			Team losingTeam = game.getTeams()
				.stream()
				.filter(Team::getWin)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("승리팀이 존재하지 않습니다."));
			Team winningTeam = game.getTeams()
				.stream()
				.filter(o -> !o.getWin())
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("패배팀이 존재하지 않습니다."));
			losingTeam.updateScore(0, false);
			winningTeam.updateScore(2, true);
			matchTournamentService.updateMatchedGameUser(game, nextMatchedGame);

			// then
			// 점수 수정으로 8강 경기에서 이긴 팀이 다음 경기로 변경되었는지 확인
			List<User> nextGameUsers = new ArrayList<>();
			nextGameUsers.add(nextMatchedGame.getTeams().get(0).getTeamUsers().get(0).getUser());
			nextGameUsers.add(nextMatchedGame.getTeams().get(1).getTeamUsers().get(0).getUser());

			assertThat(nextGameUsers.contains(winningTeam.getTeamUsers().get(0).getUser())).isTrue();
			assertThat(nextGameUsers.contains(losingTeam.getTeamUsers().get(0).getUser())).isFalse();
			verify(notiAdminService, times(2)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
		}

		@Test
		@DisplayName("우승팀이 존재하지 않을 경우 실패")
		public void failUpdateWinner() {
			// given
			// 8강 매칭
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			TournamentGame targetTournamentGame = quarterGames.get(0);
			TournamentRound nextRound = targetTournamentGame.getTournamentRound().getNextRound();
			Game nextMatchedGame = allTournamentGames.stream()
				.filter(o -> nextRound.equals(o.getTournamentRound()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("다음 경기가 존재하지 않습니다.")).getGame();

			// when, then
			assertThatThrownBy(
				() -> matchTournamentService.updateMatchedGameUser(targetTournamentGame.getGame(), nextMatchedGame))
				.isInstanceOf(WinningTeamNotFoundException.class);
		}
	}
}
