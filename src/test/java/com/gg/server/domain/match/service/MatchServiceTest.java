package com.gg.server.domain.match.service;

import com.gg.server.admin.penalty.data.PenaltyAdminRepository;
import com.gg.server.utils.MatchTestUtils;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.MatchStatusDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.dto.SlotStatusDto;
import com.gg.server.domain.match.dto.SlotStatusResponseListDto;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.PenaltyUserSlotException;
import com.gg.server.domain.match.type.MatchKey;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.type.SlotStatus;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.penalty.redis.PenaltyUserRedisRepository;
import com.gg.server.domain.penalty.redis.RedisPenaltyUser;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.rank.service.RedisUploadService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.utils.TestDataUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@RequiredArgsConstructor
class MatchServiceTest {
    @Autowired
    MatchFindService matchFindService;
    @Autowired
    MatchService matchService;
    @Autowired
    RedisMatchTimeRepository redisMatchTimeRepository;
    @Autowired
    RedisMatchUserRepository redisMatchUserRepository;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    MatchTestUtils matchTestSetting;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    RankRedisRepository rankRedisRepository;
    @Autowired
    NotiRepository notiRepository;
    @Autowired
    PenaltyAdminRepository penaltyRepository;
    @Autowired
    PenaltyUserRedisRepository penaltyUserRedisRepository;
    @Autowired
    TournamentRepository tournamentRepository;
    @Autowired
    SlotManagementRepository slotManagementRepository;
    @Autowired
    TierRepository tierRepository;
    @Autowired
    RedisUploadService redisUploadService;
    @Autowired
    TestDataUtils testDataUtils;
    List<User> users;
    List<LocalDateTime> slotTimes;

    Season testSeason;


    @BeforeEach
    void init() {
        testDataUtils.createTierSystem("pingpong");
        Random random = new Random();
        Integer userCount = random.nextInt(10) + 5;
        Integer pppGap = random.nextInt(100) + 50;
        Season season = matchTestSetting.makeTestSeason(pppGap);
        testSeason = season;
        users = new ArrayList<User>();
        for(int i = 0; i < userCount; i++) {
            User user = matchTestSetting.createUser();
            users.add(user);
        }
        users.stream().forEach(user ->
                matchTestSetting.addUsertoRankRedis(user.getId(), random.nextInt(season.getPppGap()), season.getId()));
        SlotManagement slotManagement = matchTestSetting.makeTestSlotManagement(15);
        slotTimes = matchTestSetting.getTestSlotTimes(slotManagement.getGameInterval());
    }
    @AfterEach
    void clear() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushDb();
        connection.close();
    }

    @Nested
    @DisplayName("매칭 요청 API 테스트")
    class MatchTest {
        @DisplayName("매칭 가능 상대가 없는 경우 큐에 들어감")
        @Test
        void addMatchDifferentOption() {
            System.out.println("users = " + users);
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.RANK, slotTimes.get(0));
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            Assertions.assertThat(size).isEqualTo(2L);
        }

        @DisplayName("normal both 매칭 시 게임 생성")
        @Test
        void makeGameWithNormalAndBoth() {
            System.out.println("users = " + users);
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.BOTH, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.BOTH, slotTimes.get(1));
            matchService.makeMatch(UserDto.from(users.get(3)), Option.NORMAL, slotTimes.get(1));
            Optional<Game> game1 = gameRepository.findByStartTime(slotTimes.get(0));
            Optional<Game> game2 = gameRepository.findByStartTime(slotTimes.get(1));
            Assertions.assertThat(game1).isPresent();
            Assertions.assertThat(game2).isPresent();
            System.out.println("normal user + matchService = " + matchFindService.getAllMatchStatus(UserDto.from(users.get(0)), Option.NORMAL));
            System.out.println();
            System.out.println("normal user both + matchService = " + matchFindService.getAllMatchStatus(UserDto.from(users.get(0)), Option.BOTH));
            System.out.println("both user + matchService = " + matchFindService.getAllMatchStatus(UserDto.from(users.get(1)),
                Option.BOTH));
            System.out.println("both user normal matchService = " + matchFindService.getAllMatchStatus(UserDto.from(users.get(1)),
                Option.NORMAL));
        }

        @DisplayName("Queue에 매칭 가능한 normal 상대가 있을 경우 게임 생성")
        @Test
        void addMatchSameNormalOption() {
            matchService.makeMatch(UserDto.from(users.get(2)), Option.RANK, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(0));
            matchService.cancelMatch(UserDto.from(users.get(0)), slotTimes.get(0));
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
            Assertions.assertThat(game).isEmpty();
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(slotTimes.get(0));
            Assertions.assertThat(size).isEqualTo(2L);
            RedisMatchUser remainedUser = (RedisMatchUser) redisTemplate.opsForList().index(MatchKey.getTime(slotTimes.get(0)), 0);
            Assertions.assertThat(remainedUser.getUserId()).isEqualTo(users.get(2).getId());
            Assertions.assertThat(notiRepository.findAllByUser(users.get(1)).size()).isEqualTo(2);
            Assertions.assertThat(notiRepository.findAllByUser(users.get(0)).size()).isEqualTo(1);
            Assertions.assertThat(notiRepository.findAllByUser(users.get(2)).size()).isEqualTo(0);
        }

        @DisplayName("게임 재생성 테스트")
        @Test
        void remakeGameAfterCancelling() {
            matchService.makeMatch(UserDto.from(users.get(2)), Option.RANK, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.BOTH, slotTimes.get(0));
            matchService.cancelMatch(UserDto.from(users.get(2)), slotTimes.get(0));
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
            Assertions.assertThat(game).isPresent();
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            Assertions.assertThat(size).isEqualTo(2L);
            RedisMatchUser remainedUser = (RedisMatchUser) redisTemplate.opsForList().index(MatchKey.getTime(slotTimes.get(0)), 0);
            Assertions.assertThat(remainedUser.getUserId()).isEqualTo(users.get(0).getId());
            Assertions.assertThat(notiRepository.findAllByUser(users.get(1)).size()).isEqualTo(1);
            Assertions.assertThat(notiRepository.findAllByUser(users.get(0)).size()).isEqualTo(1);
            Assertions.assertThat(notiRepository.findAllByUser(users.get(2)).size()).isEqualTo(1);
        }

        @DisplayName("Queue에 user가 선택한 random option으로 매칭 가능한 상대가 없을 경우")
        @Test
        void addMatchRankOptionAndPppGapBiggerThanSeasonPppGap() {
            User user1 = matchTestSetting.createUser();
            matchTestSetting.addUsertoRankRedis(user1.getId(),
                testSeason.getStartPpp() + testSeason.getPppGap() + 1, testSeason.getId());//pppGap차이가 충분히 큰 경우
            matchService.makeMatch(UserDto.from(user1), Option.RANK, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.RANK, slotTimes.get(0));
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            Assertions.assertThat(size).isEqualTo(3L);
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
            Assertions.assertThat(game.isEmpty()).isEqualTo(true);

        }

        @DisplayName("Queue에 user가 선택한 random option으로 매칭 가능한 상대가 있는 경우")
        @Test
        void addMatchRankOptionAndPppGapSamllerThanOrEqualToSeasonPppGap() {
            RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(testSeason.getId()), users.get(0).getId());
            User user1 = matchTestSetting.createUser();
            matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + testSeason.getPppGap()
                , testSeason.getId());//pppGap차이가 pppGap만큼
            matchService.makeMatch(UserDto.from(user1), Option.RANK, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.RANK, slotTimes.get(0));
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            // 현재 시스템 코드에서는 매칭된 슬롯은 안지우고 있음, 매칭되지 않은 슬롯만 삭제중이므로 2가 나옴
            Assertions.assertThat(size).isEqualTo(2L);
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
            Assertions.assertThat(game.isEmpty()).isEqualTo(false);
        }

        @DisplayName("Queue에 user가 선택한 both option으로 매칭 가능한 상대가 있는 경우")
        @Test
        void addMatchBothOptionAndPppGapSmallerThanOrEqualToSeasonPppGap() {
            RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(testSeason.getId()), users.get(0).getId());
            User user1 = matchTestSetting.createUser();
            matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + testSeason.getPppGap()
                , testSeason.getId());//pppGap차이가 pppGap만큼
            matchService.makeMatch(UserDto.from(user1), Option.BOTH, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, slotTimes.get(0));
            Long size = redisTemplate.opsForList().size(MatchKey.getTime(slotTimes.get(0)));
            // 현재 시스템 코드에서는 매칭된 슬롯은 안지우고 있음, 매칭되지 않은 슬롯만 삭제중이므로 2가 나옴
            Assertions.assertThat(size).isEqualTo(2L);
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
            Assertions.assertThat(game.isEmpty()).isEqualTo(false);
            Assertions.assertThat(game.get().getMode()).isEqualTo(Mode.RANK);
        }

        @DisplayName("토너먼트 시간과 겹칠 경우 게임 생성 안됨")
        @Test
        @Disabled
        void addMatchTournamentTime() {
            // given
            Tournament tournament = Tournament.builder()
                .title("test tournament")
                .contents("test contents")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .type(TournamentType.MASTER)
                .status(TournamentStatus.BEFORE)
                .build();
            tournamentRepository.save(tournament);

            // when, then
            Assertions.assertThatThrownBy(() -> matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, tournament.getStartTime()))
                .isInstanceOf(TournamentConflictException.class);

        }
    }

    @Nested
    @DisplayName("매칭 취소 API 테스트")
    class CancelMatch {
        @DisplayName("게임 생성되었을 때 경기 취소")
        @Test
        void cancelMatchAfterMakingGameEntity() {
            //normal 게임 생성
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(3));
            //user2 다른 슬롯 등록
            //첫번째 유저 경기 취소
            org.junit.jupiter.api.Assertions.assertThrows(
                EnrolledSlotException.class,
                () -> matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0))
            );
            matchService.cancelMatch(UserDto.from(users.get(0)), slotTimes.get(3));
            Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(3));
            Assertions.assertThat(game.isEmpty()).isEqualTo(true);
            // 취소한 유저는 큐에 없음
            Assertions.assertThat(redisMatchUserRepository.countMatchTime(users.get(0).getId())).isEqualTo(0L);
            // 취소하지 않은 유저는 큐에 유지
            Assertions.assertThat(redisMatchUserRepository.countMatchTime(users.get(1).getId())).isEqualTo(1L);

            //알람 확인
            List<Noti> notifications = notiRepository.findAllByUser(users.get(1));
            System.out.println("users.get(0).getIntraId() = " + users.get(0).getIntraId());
            for (Noti noti : notifications) {
                System.out.println("noti.getMessage() = " + noti.getMessage());
            }
            Assertions.assertThat(notifications.size()).isEqualTo(2);
            Assertions.assertThat(notifications.get(0).getType()).isEqualTo(NotiType.MATCHED);
            Assertions.assertThat(notifications.get(1).getType()).isEqualTo(NotiType.CANCELEDBYMAN);

            //패널티 확인
            Optional<RedisPenaltyUser> penaltyUser = penaltyUserRedisRepository.findByIntraId(users.get(0).getIntraId());
            Assertions.assertThat(penaltyUser).isPresent();
            // 패널티 시간은 하드 코딩 시 관리가 어려우니 Static이나 enum으로 관리하는게 좋을 듯 하다.
            Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(30);
            org.junit.jupiter.api.Assertions.assertThrows(PenaltyUserSlotException.class, () -> {
                    matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, slotTimes.get(10));
                }
            );
        }

        @DisplayName("게임 생성 전 경기 취소")
        @Test
        void cancelBeforeMakingGameEntity() {
            RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(testSeason.getId()), users.get(0).getId());
            User user1 = matchTestSetting.createUser();
            matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + testSeason.getPppGap() + 100
                , testSeason.getId());
            //매칭이 이루어질 수 없는 유저 3명을 큐에 등록
            matchService.makeMatch(UserDto.from(users.get(0)), Option.RANK, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(user1), Option.RANK, slotTimes.get(0));
            //user1의 취소
            matchService.cancelMatch(UserDto.from(users.get(1)), slotTimes.get(0));
            List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(slotTimes.get(0));
            Assertions.assertThat(allMatchUsers.size()).isEqualTo(2L);

        }
    }

    @Nested
    @DisplayName("특정 시간대의 경기 매칭 가능 상태 조회 API 테스트")
    class GetMatchTimeScope {
        @DisplayName("슬롯 조회 : 게임 생성한 후 내 테이블로 인식")
        @Test
        void readMyTableAfterMakingGame() {
            //normal 게임 생성
            matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(0));
            matchService.makeMatch(UserDto.from(users.get(0)), Option.RANK, slotTimes.get(4));
            matchService.makeMatch(UserDto.from(users.get(1)), Option.RANK, slotTimes.get(1));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.NORMAL, slotTimes.get(2));
            SlotStatusResponseListDto slotStatusList = matchFindService.getAllMatchStatus(UserDto.from(users.get(0)),
                Option.NORMAL);
            for (int i = 0; i < 3; i++) {
                System.out.println("slotTimes = " + String.valueOf(i) + slotTimes.get(i));
            }
            for (List<SlotStatusDto> dtos : slotStatusList.getMatchBoards()) {
                for (SlotStatusDto dto: dtos) {
                    System.out.println("dto = " + dto);
                    if (dto.getStartTime().equals(slotTimes.get(0))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MYTABLE.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(1))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.OPEN.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(2))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MATCH.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(4))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.CLOSE.getCode());
                    }
                }
            }

        }

        @DisplayName("슬롯 조회 : 게임 생성 전 내 테이블로 인식")
        @Test
        void readMyTableBeforeMakingGame() {
            for (int i = 0; i < 3; i++) {
                matchService.makeMatch(UserDto.from(users.get(0)), Option.NORMAL, slotTimes.get(i));
            }
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.NORMAL, slotTimes.get(3));
            SlotStatusResponseListDto slotStatusList = matchFindService.getAllMatchStatus(UserDto.from(users.get(0)),
                Option.NORMAL);
            for (List<SlotStatusDto> dtos : slotStatusList.getMatchBoards()) {
                for (SlotStatusDto dto: dtos) {
                    if (dto.getStartTime().equals(slotTimes.get(0))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MYTABLE.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(1))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MYTABLE.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(2))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MYTABLE.getCode());
                    }
                    if (dto.getStartTime().equals(slotTimes.get(3))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.CLOSE.getCode());
                    }
                }
            }

        }

        @DisplayName("슬롯 조회 : 게임 등록 되면 제 3자한테 closed 처리")
        @Test
        void getClosedStatusOfExistGame() {
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.NORMAL, slotTimes.get(3));
            SlotStatusResponseListDto allMatchStatus = matchFindService.getAllMatchStatus(UserDto.from(users.get(3)),
                Option.NORMAL);
            for (List<SlotStatusDto> dtos : allMatchStatus.getMatchBoards()) {
                for (SlotStatusDto dto: dtos) {
                    if (dto.getStartTime().equals(slotTimes.get(3))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.CLOSE.getCode());
                    }
                }
            }
        }

        @DisplayName("슬롯 조회 : 게임 등록 되면 제3자가 다른 게임 등록해도 제 3자한테 closed 처리")
        @Test
        void getClosedStatusOfExistGame2() {
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(3)), Option.NORMAL, slotTimes.get(4));
            matchService.makeMatch(UserDto.from(users.get(4)), Option.NORMAL, slotTimes.get(4));
            SlotStatusResponseListDto allMatchStatus = matchFindService.getAllMatchStatus(UserDto.from(users.get(3)),
                Option.NORMAL);
            for (List<SlotStatusDto> dtos : allMatchStatus.getMatchBoards()) {
                for (SlotStatusDto dto: dtos) {
                    if (dto.getStartTime().equals(slotTimes.get(3))) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.CLOSE.getCode());
                    }
                }
            }
        }

        @DisplayName("토너먼트 시간에 대한 슬롯 블락")
        @Test
        @Disabled
        void getClosedStatusOfTournament() {
            // given
            Tournament tournament = Tournament.builder()
                .title("test tournament")
                .contents("test contents")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .type(TournamentType.MASTER)
                .status(TournamentStatus.BEFORE)
                .build();
            tournamentRepository.save(tournament);
            tierRepository.save(new Tier("image url"));

            // TODO 현재 upload redis해도 redis 데이터 없다는 에러 발생함
            redisUploadService.uploadRedis();
            // when
            LocalDateTime now = LocalDateTime.now();
            SlotStatusResponseListDto allMatchStatus = matchFindService.getAllMatchStatus(UserDto.from(users.get(0)),
                Option.NORMAL);

            // then
            for (List<SlotStatusDto> dtos : allMatchStatus.getMatchBoards()) {
                for (SlotStatusDto dto: dtos) {
                    if (dto.getStartTime().isBefore(tournament.getStartTime()) ||
                        dto.getEndTime().isAfter(tournament.getEndTime())) {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.OPEN.getCode());
                    } else {
                        Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.CLOSE.getCode());
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("현재 매치 정보 조회 API 테스트")
    class GetCurrentMatch {
        @DisplayName("current Match 조회 : user가 등록한 슬롯이 매칭되었을 때")
        @Test
        void readCurrentMatchAfterMakingGameEntity() {
            //게임생성
            matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(3));
            matchService.makeMatch(UserDto.from(users.get(2)), Option.NORMAL, slotTimes.get(3));
            UserDto userDto = UserDto.from(users.get(1));
            MatchStatusResponseListDto currentMatch = matchFindService.getCurrentMatch(userDto);
            //user의 current match 확인
            List<MatchStatusDto> match = currentMatch.getMatch();
            Assertions.assertThat(match.size()).isEqualTo(1);
            Assertions.assertThat(match.get(0).getMyTeam().get(0)).isEqualTo(users.get(1).getIntraId());
            Assertions.assertThat(match.get(0).getEnemyTeam().get(0)).isEqualTo(users.get(2).getIntraId());
            Assertions.assertThat(match.get(0).getStartTime()).isEqualTo(slotTimes.get(3));
            Assertions.assertThat(match.get(0).getIsMatched()).isEqualTo(true);
        }

        @DisplayName("current Match 조회 : user가 등록한 슬롯이 매칭되지 않았을 때")
        @Test
        void readCurrentMatchBeforeMakingGameEntity() {
            //유저 슬롯 3개 등록 시도
            for (int i = 0; i < 3; i++) {
                System.out.println("slotTimes = " + slotTimes.get(i));
                matchService.makeMatch(UserDto.from(users.get(1)), Option.NORMAL, slotTimes.get(i));
            }
            UserDto userDto = UserDto.from(users.get(1));
            MatchStatusResponseListDto currentMatch = matchFindService.getCurrentMatch(userDto);
            List<MatchStatusDto> match = currentMatch.getMatch();
            //user current match 확인
            Assertions.assertThat(match.size()).isEqualTo(3);
            for (int i = 0; i < 3; i++) {
                System.out.println("match = " + match.get(i).getStartTime());
                Assertions.assertThat(match.get(i).getMyTeam().size()).isEqualTo(0);
                Assertions.assertThat(match.get(i).getEnemyTeam().size()).isEqualTo(0);
                Assertions.assertThat(match.get(i).getStartTime()).isEqualTo(slotTimes.get(i));
                Assertions.assertThat(match.get(i).getIsMatched()).isEqualTo(false);
            }
        }

        @DisplayName("Guest User slot 조회")
        @Test
        void readAllSlotsAndCurrentMatchForGuset() {
            User guestUser = matchTestSetting.createGuestUser();
            MatchStatusResponseListDto currentMatch = matchFindService.getCurrentMatch(UserDto.from(guestUser));
            SlotStatusResponseListDto allMatchStatus = matchFindService.getAllMatchStatus(UserDto.from(guestUser),
                Option.NORMAL);
            Assertions.assertThat(currentMatch.getMatch().size()).isEqualTo(0);
            System.out.println("allMatchStatus = " + allMatchStatus);
        }

    }
}
