package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.utils.SlotGenerator;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.utils.annotation.UnitTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.setFieldWithReflection;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private Game createGame(User user, User enemy) {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        Game game = new Game(season, StatusType.BEFORE, Mode.NORMAL, startTime, startTime.plusMinutes(15));
        Team teamA = new Team(game, -1, false);
        Team teamB = new Team(game, -1, false);
        new TeamUser(teamA, user);
        new TeamUser(teamB, enemy);
        setFieldWithReflection(game, "id", 1L);
        return game;
    }

    @Nested
    @DisplayName("현재 내가 등록한 슬롯 정보 가져오기")
    class getCurrentMatch {
        private final User user = User.builder().roleType(RoleType.USER).intraId("hannkim").build();

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
            given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(Optional.of(slotManagement));
            given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, user.getId())).willReturn(Optional.empty());
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
            User enemy = User.builder().roleType(RoleType.USER).intraId("enemy").build();
            setFieldWithReflection(enemy, "id", 2L);
            Game myGame = createGame(user, enemy);
            given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(Optional.of(slotManagement));
            given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, user.getId())).willReturn(Optional.of(myGame));
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
    class getAllMatchStatus {
        private final User user = User.builder().roleType(RoleType.USER).intraId("hannkim").build();
        private SlotGenerator slotGenerator;

        @BeforeEach
        void init() {
            setFieldWithReflection(user, "id", 1L);
            given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(Optional.of(slotManagement));
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
            RankRedis redisUser = RankRedis.from(userDto, season.getStartPpp(), tier.getImageUri());
            String hashKey = RedisKeyManager.getHashKey(season.getId());
            slotGenerator = new SlotGenerator(redisUser, slotManagement, season, Option.BOTH);
            given(rankRedisRepository.findRankByUserId(hashKey, user.getId())).willReturn(redisUser);       // guest가 아닌 유저일 경우
            given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId())).willReturn(Optional.empty());
            given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
            given(redisMatchUserRepository.getAllMatchTime(slotGenerator.getMatchUser().getUserId())).willReturn(Set.of());
            given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(List.of());   // TODO 수정

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
            User guest = User.builder().intraId("guest").roleType(RoleType.GUEST).build();
            RankRedis redisUser = RankRedis.from(UserDto.from(guest), season.getStartPpp(), tier.getImageUri());
            slotGenerator = new SlotGenerator(redisUser, slotManagement, season, Option.BOTH);
            given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(guest).getId())).willReturn(Optional.empty());
            given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
            given(redisMatchUserRepository.getAllMatchTime(slotGenerator.getMatchUser().getUserId())).willReturn(Set.of());
            given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(List.of());   // TODO 수정

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
            RankRedis redisUser = RankRedis.from(userDto, season.getStartPpp(), tier.getImageUri());
            String hashKey = RedisKeyManager.getHashKey(season.getId());
            User enemy = User.builder().roleType(RoleType.USER).intraId("enemy").build();
            Game game = createGame(user, enemy);
            setFieldWithReflection(enemy, "id", 2L);
            setFieldWithReflection(game, "id", 1L);
            given(rankRedisRepository.findRankByUserId(hashKey, user.getId())).willReturn(redisUser);
            given(gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, UserDto.from(user).getId())).willReturn(Optional.of(game));
            given(redisMatchTimeRepository.getAllEnrolledStartTimes()).willReturn(Set.of(LocalDateTime.now()));
            given(redisMatchTimeRepository.getAllMatchUsers(any(LocalDateTime.class))).willReturn(List.of());   // TODO 수정

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
