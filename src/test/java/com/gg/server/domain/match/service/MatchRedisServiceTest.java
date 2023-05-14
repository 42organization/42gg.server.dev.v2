package com.gg.server.domain.match.service;

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
import com.gg.server.domain.match.type.MatchKey;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.type.SlotStatus;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("local")
@RequiredArgsConstructor
class MatchRedisServiceTest {
    @Autowired
    MatchRedisService matchRedisService;
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

    List<User> users;
    List<LocalDateTime> slotTimes;

    Season testSeason;


    @BeforeEach
    void init() {
        Random random = new Random();
        Integer userCount = random.nextInt(10) + 5;
        Integer pppGap = random.nextInt(100) + 50;
        Season season = matchTestSetting.makeTestSeason(pppGap);
        this.testSeason = season;
        for(int i = 0; i < userCount; i++) {
            matchTestSetting.createUser();
        }
        List<User> users = matchTestSetting.findAllUsers();
        this.users = users;
        users.stream().forEach(user ->
                matchTestSetting.addUsertoRankRedis(user.getId(), random.nextInt(season.getPppGap()), season.getId()));
        SlotManagement slotManagement = matchTestSetting.makeTestSlotManagement(15);
        List<LocalDateTime> slotTimes = matchTestSetting.getTestSlotTimes(slotManagement.getGameInterval());
        this.slotTimes = slotTimes;
    }
    @AfterEach
    void clear() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushDb();
        connection.close();
    }

    @DisplayName("매칭 가능 상대가 없는 경우 큐에 들어감")
    @Test
    void addMatchDifferentOption() {
        System.out.println("this.users = " + this.users);
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(1).getId(), Option.RANK, this.slotTimes.get(0));
        Long size = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + slotTimes.get(0));
        Assertions.assertThat(size).isEqualTo(2L);
    }

    @DisplayName("Queue에 매칭 가능한 normal 상대가 있을 경우 게임 생성")
    @Test
    void addMatchSameNormalOption() {
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, this.slotTimes.get(0));
        Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
        Assertions.assertThat(game.isEmpty()).isEqualTo(false);
    }

    @DisplayName("Queue에 user가 선택한 random option으로 매칭 가능한 상대가 없을 경우")
    @Test
    void addMatchRankOptionAndPppGapBiggerThanSeasonPppGap() {
        User user1 = matchTestSetting.createUser();
        matchTestSetting.addUsertoRankRedis(user1.getId(),
                this.testSeason.getStartPpp() + this.testSeason.getPppGap() + 1, this.testSeason.getId());//pppGap차이가 충분히 큰 경우
        matchRedisService.makeMatch(user1.getId(), Option.RANK, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(1).getId(), Option.RANK, this.slotTimes.get(0));
        Long size = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + slotTimes.get(0));
        Assertions.assertThat(size).isEqualTo(3L);
        Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
        Assertions.assertThat(game.isEmpty()).isEqualTo(true);

    }

    @DisplayName("Queue에 user가 선택한 random option으로 매칭 가능한 상대가 있는 경우")
    @Test
    void addMatchRankOptionAndPppGapSamllerThanOrEqualToSeasonPppGap() {
        RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(this.testSeason.getId()), users.get(0).getId());
        User user1 = matchTestSetting.createUser();
        matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + this.testSeason.getPppGap()
                , this.testSeason.getId());//pppGap차이가 pppGap만큼
        matchRedisService.makeMatch(user1.getId(), Option.RANK, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(0).getId(), Option.RANK, this.slotTimes.get(0));
        Long size = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + slotTimes.get(0));
        Assertions.assertThat(size).isEqualTo(0L);
        Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
        Assertions.assertThat(game.isEmpty()).isEqualTo(false);
    }

    @DisplayName("Queue에 user가 선택한 both option으로 매칭 가능한 상대가 있는 경우")
    @Test
    void addMatchBothOptionAndPppGapSmallerThanOrEqualToSeasonPppGap() {
        RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(this.testSeason.getId()), users.get(0).getId());
        User user1 = matchTestSetting.createUser();
        matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + this.testSeason.getPppGap()
                , this.testSeason.getId());//pppGap차이가 pppGap만큼
        matchRedisService.makeMatch(user1.getId(), Option.BOTH, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(0).getId(), Option.BOTH, this.slotTimes.get(0));
        Long size = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + slotTimes.get(0));
        Assertions.assertThat(size).isEqualTo(0L);
        Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
        Assertions.assertThat(game.isEmpty()).isEqualTo(false);
        Assertions.assertThat(game.get().getMode()).isEqualTo(Mode.RANK);
    }


    @DisplayName("게임 생성되었을 때 경기 취소")
    @Test
    void cancelMatchAfterMakingGameEntity() {
        //normal 게임 생성
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, this.slotTimes.get(0));
        //user2 다른 슬롯 등록
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, this.slotTimes.get(1));

        //첫번째 유저 경기 취소
        matchRedisService.cancelMatch(users.get(0).getId(), slotTimes.get(0));

        Optional<Game> game = gameRepository.findByStartTime(slotTimes.get(0));
        Assertions.assertThat(game.isEmpty()).isEqualTo(true);
        Assertions.assertThat(redisMatchUserRepository.countMatchTime(users.get(1).getId())).isEqualTo(1L);
    }

    @DisplayName("게임 생성 전 경기 취소")
    @Test
    void cancelBeforeMakingGameEntity() {
        RankRedis userRank = rankRedisRepository.findRankByUserId(RedisKeyManager
                .getHashKey(this.testSeason.getId()), users.get(0).getId());
        User user1 = matchTestSetting.createUser();
        matchTestSetting.addUsertoRankRedis(user1.getId(),userRank.getPpp() + this.testSeason.getPppGap() + 100
                , this.testSeason.getId());
        //매칭이 이루어질 수 없는 유저 3명을 큐에 등록
        matchRedisService.makeMatch(users.get(0).getId(), Option.RANK, slotTimes.get(0));
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, slotTimes.get(0));
        matchRedisService.makeMatch(user1.getId(), Option.RANK, slotTimes.get(0));
        //user1의 취소
        matchRedisService.cancelMatch(users.get(1).getId(), slotTimes.get(0));
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(slotTimes.get(0));
        Assertions.assertThat(allMatchUsers.size()).isEqualTo(2L);

    }

    @DisplayName("슬롯 조회 : 게임 생성한 후 내 테이블로 인식")
    @Test
    void readMyTableAfterMakingGame() {
        //normal 게임 생성
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(0));
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(1));
        matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, this.slotTimes.get(2));
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, this.slotTimes.get(0));
        SlotStatusResponseListDto slotStatusList = matchRedisService.getAllMatchStatus(users.get(0).getId(),
                Option.NORMAL);
        for (SlotStatusDto dto : slotStatusList.getMatchBoards()) {
            if (dto.getStartTime().equals(slotTimes.get(0))) {
                Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.MYTABLE.getCode());
            }
            if (dto.getStartTime().equals(slotTimes.get(1))) {
                Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.OPEN.getCode());
            }
            if (dto.getStartTime().equals(slotTimes.get(2))) {
                Assertions.assertThat(dto.getStatus()).isEqualTo(SlotStatus.OPEN.getCode());
            }
        }

    }

    @DisplayName("슬롯 조회 : 게임 생성 전 내 테이블로 인식")
    @Test
    void readMyTableBeforeMakingGame() {
        for (int i = 0; i < 3; i++) {
            matchRedisService.makeMatch(users.get(0).getId(), Option.NORMAL, slotTimes.get(i));
        }
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, slotTimes.get(3));
        matchRedisService.makeMatch(users.get(2).getId(), Option.NORMAL, slotTimes.get(3));
        SlotStatusResponseListDto slotStatusList = matchRedisService.getAllMatchStatus(users.get(0).getId(),
                Option.NORMAL);
        for (SlotStatusDto dto : slotStatusList.getMatchBoards()) {
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
    @DisplayName("current Match 조회 : user가 등록한 슬롯이 매칭되었을 때")
    @Test
    void readCurrentMatchAfterMakingGameEntity() {
        //게임생성
        matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, slotTimes.get(3));
        matchRedisService.makeMatch(users.get(2).getId(), Option.NORMAL, slotTimes.get(3));
        UserDto userDto = UserDto.from(users.get(1));
        MatchStatusResponseListDto currentMatch = matchRedisService.getCurrentMatch(userDto);
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
        //유저 슬롯 4개 등록 시도
        for (int i = 0; i < 4; i++) {
            matchRedisService.makeMatch(users.get(1).getId(), Option.NORMAL, slotTimes.get(i));
        }
        UserDto userDto = UserDto.from(users.get(1));
        MatchStatusResponseListDto currentMatch = matchRedisService.getCurrentMatch(userDto);
        List<MatchStatusDto> match = currentMatch.getMatch();
        //user current match 확인
        Assertions.assertThat(match.size()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            Assertions.assertThat(match.get(i).getMyTeam().size()).isEqualTo(0);
            Assertions.assertThat(match.get(i).getEnemyTeam().size()).isEqualTo(0);
            Assertions.assertThat(match.get(i).getStartTime()).isEqualTo(slotTimes.get(i));
            Assertions.assertThat(match.get(i).getIsMatched()).isEqualTo(false);
        }
    }
}
