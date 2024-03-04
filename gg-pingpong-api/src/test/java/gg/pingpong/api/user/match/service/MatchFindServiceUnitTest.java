package gg.pingpong.api.user.match.service;

import static gg.pingpong.api.user.match.utils.GameTestUtils.*;
import static gg.pingpong.api.user.match.utils.UserTestUtils.*;
import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.manage.SlotManagement;
import gg.data.pingpong.match.RedisMatchTime;
import gg.data.pingpong.match.type.Option;
import gg.data.pingpong.rank.Tier;
import gg.data.pingpong.rank.redis.RankRedis;
import gg.data.pingpong.season.Season;
import gg.data.user.User;
import gg.pingpong.api.user.match.utils.SlotGenerator;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.game.GameRepository;
import gg.repo.manage.SlotManagementRepository;
import gg.repo.match.RedisMatchTimeRepository;
import gg.repo.match.RedisMatchUserRepository;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.user.UserRepository;
import gg.utils.RedisKeyManager;
import gg.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class MatchFindServiceUnitTest {
	@InjectMocks
	MatchFindService matchFindService;
	@Mock
	private SlotManagementRepository slotManagementRepository;
	@Mock
	private GameRepository gameRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RedisMatchUserRepository redisMatchUserRepository;
	@Mock
	private SeasonFindService seasonFindService;
	@Mock
	private RankRedisRepository rankRedisRepository;
	@Mock
	private RedisMatchTimeRepository redisMatchTimeRepository;
	@Mock
	private TierRepository tierRepository;

	private static final SlotManagement slotManagement = SlotManagement.builder()
		.pastSlotTime(0)
		.futureSlotTime(12)
		.openMinute(5)
		.gameInterval(15)
		.startTime(LocalDateTime.now())
		.build();
	private static final Season season = Season.builder().startTime(LocalDateTime.now()).startPpp(123).build();
	private static final Tier tier = new Tier();

	@BeforeEach
	public void init() {
		setFieldWithReflection(slotManagement, "id", 1L);
		setFieldWithReflection(season, "id", 1L);
		setFieldWithReflection(tier, "id", 1L);
	}

	@Nested
	@DisplayName("현재 내가 등록한 슬롯 정보 가져오기")
	class GetCurrentMatch {
		private final User user = createUser();

		@BeforeEach
		public void init() {
			setFieldWithReflection(user, "id", 1L);
		}

		@Test
		@DisplayName("등록한 슬롯(최대 3개) 정보를 가져온다.")
		void successUnmatchedSlots() {
			// given
			UserDto userDto = UserDto.from(user);
			LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
			RedisMatchTime redisMatchTime = new RedisMatchTime(startTime, Option.BOTH);
			given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(
				Optional.of(slotManagement));
			given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, user.getId())).willReturn(
				Optional.empty());
			given(redisMatchUserRepository.getAllMatchTime(user.getId())).willReturn(Set.of(redisMatchTime));

			// when
			matchFindService.getCurrentMatch(userDto);

			// then
			verify(slotManagementRepository, times(1)).findCurrent(any(LocalDateTime.class));
			verify(gameRepository, times(1)).findByStatusTypeAndUserId(StatusType.BEFORE, user.getId());
			verify(redisMatchUserRepository, times(1)).getAllMatchTime(user.getId());
		}

		@Test
		@DisplayName("매칭된 슬롯(1개) 정보를 가져온다.")
		void successMatchedSlot() {
			// given
			UserDto userDto = UserDto.from(user);
			User enemy = createUser();
			Game myGame = createNormalGame(user, enemy, season);
			setFieldWithReflection(enemy, "id", 2L);
			setFieldWithReflection(myGame, "id", 1L);
			given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(
				Optional.of(slotManagement));
			given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, user.getId())).willReturn(
				Optional.of(myGame));
			given(userRepository.findEnemyByGameAndUser(myGame.getId(), user.getId())).willReturn(List.of(enemy));

			// when
			matchFindService.getCurrentMatch(userDto);

			// then
			verify(slotManagementRepository, times(1)).findCurrent(any(LocalDateTime.class));
			verify(gameRepository, times(1)).findByStatusTypeAndUserId(StatusType.BEFORE, user.getId());
			verify(userRepository, times(1)).findEnemyByGameAndUser(myGame.getId(), user.getId());
		}

	}

	@Nested
	@DisplayName("경기 매칭 가능 상태 조회")
	class GetAllMatchStatus {
		private final User user = createUser();
		private SlotGenerator slotGenerator;

		@BeforeEach
		void init() {
			setFieldWithReflection(user, "id", 1L);
			given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(
				Optional.of(slotManagement));
			given(seasonFindService.findCurrentSeason(any(LocalDateTime.class))).willReturn(season);
			given(tierRepository.findStartTier()).willReturn(Optional.of(tier));
			List<Game> games = List.of();       // TODO 수정? 또는 slogGenerator에서 검증?
			given(gameRepository.findAllBetween(any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(games);
		}

		@AfterEach
		void verifyEach() {
			verify(slotManagementRepository, times(1)).findCurrent(any(LocalDateTime.class));
			verify(seasonFindService, times(1)).findCurrentSeason(any(LocalDateTime.class));
			verify(tierRepository, times(1)).findStartTier();
			verify(gameRepository, times(1)).findAllBetween(any(LocalDateTime.class), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("슬롯 정보를 가져온다. - 내가 등록한 슬롯이 없는 경우")
		void success() {
			// given
			UserDto userDto = UserDto.from(user);
			RankRedis redisUser = RankRedis.from(userDto.getId(), userDto.getIntraId(), userDto.getTextColor(),
				season.getStartPpp(), tier.getImageUri());
			String hashKey = RedisKeyManager.getHashKey(season.getId());
			slotGenerator = new SlotGenerator(redisUser, slotManagement, season, Option.BOTH);
			given(rankRedisRepository.findRankByUserId(hashKey, user.getId())).willReturn(
				redisUser);       // guest가 아닌 유저일 경우
			given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId())).willReturn(
				Optional.empty());
			given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
			given(redisMatchUserRepository.getAllMatchTime(slotGenerator.getMatchUser().getUserId())).willReturn(
				Set.of());
			given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(
				List.of());   // TODO 수정

			// when
			matchFindService.getAllMatchStatus(userDto, Option.BOTH);

			// then
			verify(rankRedisRepository, times(1)).findRankByUserId(hashKey, user.getId());
			verify(gameRepository, times(1)).findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId());
			verify(redisMatchTimeRepository, times(1)).getAllEnrolledStartTimes();
			verify(redisMatchUserRepository, times(1)).getAllMatchTime(slotGenerator.getMatchUser().getUserId());
			verify(redisMatchTimeRepository, times(1)).getAllMatchUsers(any(LocalDateTime.class));

			// TODO slotGenerator 검증 (response dto)
		}

		@Test
		@DisplayName("GUEST 유저가 슬롯 정보를 가져온다. - 내가 등록한 슬롯 없는 경우")
		void successGuest() {
			// given
			User guest = createGuestUser();
			RankRedis redisUser = RankRedis.from(guest.getId(), guest.getIntraId(), guest.getTextColor(),
				season.getStartPpp(), tier.getImageUri());
			slotGenerator = new SlotGenerator(redisUser, slotManagement, season, Option.BOTH);
			given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(guest).getId())).willReturn(
				Optional.empty());
			given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
			given(redisMatchUserRepository.getAllMatchTime(slotGenerator.getMatchUser().getUserId())).willReturn(
				Set.of());
			given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(
				List.of());   // TODO 수정

			// when
			matchFindService.getAllMatchStatus(UserDto.from(guest), Option.BOTH);

			// then
			verify(gameRepository, times(1)).findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(guest).getId());
			verify(redisMatchTimeRepository, times(1)).getAllEnrolledStartTimes();
			verify(redisMatchUserRepository, times(1)).getAllMatchTime(slotGenerator.getMatchUser().getUserId());
			verify(redisMatchTimeRepository, times(1)).getAllMatchUsers(any(LocalDateTime.class));
		}

		@Test
		@DisplayName("슬롯 정보를 가져온다. - 내가 등록한 슬롯이 있는 경우")
		void successWithMySlot() {
			// given
			UserDto userDto = UserDto.from(user);
			RankRedis redisUser = RankRedis.from(userDto.getId(), userDto.getIntraId(), userDto.getTextColor(),
				season.getStartPpp(), tier.getImageUri());
			String hashKey = RedisKeyManager.getHashKey(season.getId());
			User enemy = createUser();
			Game game = createNormalGame(user, enemy, season);
			setFieldWithReflection(enemy, "id", 2L);
			setFieldWithReflection(game, "id", 1L);
			given(rankRedisRepository.findRankByUserId(hashKey, user.getId())).willReturn(redisUser);
			given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId())).willReturn(
				Optional.of(game));
			given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
			given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(
				List.of());   // TODO 수정

			// when
			matchFindService.getAllMatchStatus(userDto, Option.BOTH);

			// then
			verify(rankRedisRepository, times(1)).findRankByUserId(hashKey, user.getId());
			verify(gameRepository, times(1)).findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId());
			verify(redisMatchTimeRepository, times(1)).getAllEnrolledStartTimes();
			verify(redisMatchTimeRepository, times(1)).getAllMatchUsers(any(LocalDateTime.class));
		}
	}
}
