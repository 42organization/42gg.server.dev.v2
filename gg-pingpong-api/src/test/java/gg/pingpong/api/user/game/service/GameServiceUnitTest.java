package gg.pingpong.api.user.game.service;

import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.game.type.StatusType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.user.game.controller.request.RankResultReqDto;
import gg.pingpong.api.user.match.service.MatchTournamentService;
import gg.pingpong.api.user.rank.redis.RankRedisService;
import gg.pingpong.api.user.rank.service.TierService;
import gg.pingpong.api.user.store.service.UserCoinChangeService;
import gg.repo.game.GameRepository;
import gg.repo.game.PChangeRepository;
import gg.repo.game.TeamUserRepository;
import gg.repo.game.out.GameTeamUserInfo;
import gg.repo.tournarment.TournamentGameRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.game.GameNotExistException;

@UnitTest
class GameServiceUnitTest {
	@Mock
	GameRepository gameRepository;
	@Mock
	TeamUserRepository teamUserRepository;
	@Mock
	RankRedisService rankRedisService;
	@Mock
	PChangeService pChangeService;
	@Mock
	PChangeRepository pChangeRepository;
	@Mock
	GameFindService gameFindService;
	@Mock
	UserCoinChangeService userCoinChangeService;
	@Mock
	TierService tierService;
	@Mock
	TournamentGameRepository tournamentGameRepository;
	@Mock
	MatchTournamentService matchTournamentService;
	@InjectMocks
	GameService gameService;

	@Nested
	@DisplayName("getUserGameInfo 매서드 유닛 테스트")
	class GetUserGameInfo {
		List<GameTeamUserInfo> infos;

		@BeforeEach
		void beforeEach() {
			int infoCnt = 5;
			infos = new ArrayList<>();
			for (int i = 0; i < infoCnt; i++) {
				GameTeamUserInfo info = mock(GameTeamUserInfo.class);
				infos.add(info);
			}
		}

		@Test
		@DisplayName("GameNotExistException")
		void gameNotExistException() {
			// given
			given(gameRepository.findTeamGameUser(any())).willReturn(new ArrayList<>());
			// when, then
			assertThatThrownBy(() -> gameService.getUserGameInfo(1L, 1L))
				.isInstanceOf(GameNotExistException.class);
		}
	}

	@Nested
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("createRankResult 매서드 유닛 테스트")
	class CreateRankResult {
		RankResultReqDto scoreDto;
		Game game;
		List<TeamUser> teams;
		Team myTeam;
		Team enemyTeam;
		User me;
		User enemy;

		@BeforeEach
		void beforeEach() {
			game = new Game();
			setFieldWithReflection(game, "id", 1L);
			setFieldWithReflection(game, "startTime", LocalDateTime.now());

			myTeam = new Team(game, 2, true);
			setFieldWithReflection(myTeam, "id", 1L);
			enemyTeam = new Team(game, 1, false);
			setFieldWithReflection(enemyTeam, "id", 2L);

			me = new User("", "", "", RacketType.SHAKEHAND,
				RoleType.USER, 0, SnsType.NONE, 1L);
			enemy = new User("", "", "", RacketType.SHAKEHAND,
				RoleType.USER, 0, SnsType.NONE, 1L);
			setFieldWithReflection(me, "id", 1L);
			setFieldWithReflection(enemy, "id", 2L);

			teams = new ArrayList<>();
			teams.add(new TeamUser(1L, myTeam, me));
			teams.add(new TeamUser(2L, enemyTeam, enemy));

			scoreDto = new RankResultReqDto(1L, myTeam.getId(), 2,
				enemyTeam.getId(), 1);
		}

		@ParameterizedTest
		@EnumSource(value = StatusType.class)
		@DisplayName("success -> false")
		void successFalse(StatusType type) {
			// given
			setFieldWithReflection(game, "status", type);
			given(gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId())).willReturn(game);
			given(teamUserRepository.findAllByGameId(game.getId())).willReturn(teams);
			// when
			Boolean result = gameService.createRankResult(scoreDto, me.getId());
			// then
			assertThat(result).isEqualTo(false);
		}

		@ParameterizedTest
		@EnumSource(value = StatusType.class, names = {"WAIT", "LIVE"})
		@DisplayName("success -> true")
		void successTrue(StatusType type) {
			// given
			setFieldWithReflection(game, "status", type);
			setFieldWithReflection(myTeam, "score", -1);
			setFieldWithReflection(enemyTeam, "score", -1);
			given(gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId())).willReturn(game);
			given(teamUserRepository.findAllByGameId(game.getId())).willReturn(teams);
			// when
			Boolean result = gameService.createRankResult(scoreDto, me.getId());
			// then
			assertThat(result).isEqualTo(true);
			assertThat(myTeam.getScore()).isEqualTo(scoreDto.getMyTeamScore());
			assertThat(enemyTeam.getScore()).isEqualTo(scoreDto.getEnemyTeamScore());
		}

		@ParameterizedTest
		@EnumSource(value = StatusType.class, names = {"WAIT", "LIVE"})
		@DisplayName("InvalidParameterException")
		void invalidParameterException(StatusType type) {
			// given
			setFieldWithReflection(game, "status", type);
			given(gameFindService.findGameWithPessimisticLockById(scoreDto.getGameId())).willReturn(game);
			given(teamUserRepository.findAllByGameId(game.getId())).willReturn(teams);
			// when, then
			assertThatThrownBy(() -> gameService.createRankResult(scoreDto, enemy.getId()))
				.isInstanceOf(InvalidParameterException.class);
		}
	}
}
