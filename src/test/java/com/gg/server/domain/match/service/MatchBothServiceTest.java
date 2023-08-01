package com.gg.server.domain.match.service;

import com.gg.server.admin.penalty.data.PenaltyAdminRepository;
import com.gg.server.admin.penalty.type.PenaltyKey;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.penalty.redis.PenaltyUserRedisRepository;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class MatchBothServiceTest {
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
    PenaltyAdminRepository penaltyAdminRepository;
    @Autowired
    PenaltyUserRedisRepository penaltyUserRedisRepository;
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
        List<User> users = new ArrayList<User>();
        for(int i = 0; i < userCount; i++) {
            User user = matchTestSetting.createUser();
            users.add(user);
        }
        this.users = users;
        users.stream().forEach(user ->
                matchTestSetting.addUsertoRankRedis(user.getId(), 1000, season.getId()));
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

    @DisplayName("both 매칭 시뮬레이션")
    @Test
    void makeGameWithNormalAndBoth() {
        System.out.println("this.users = " + this.users);
        // 유저 0 slot 2, 3, 0 등록 유저 1 slot 2 
        matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, this.slotTimes.get(2));
        matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, this.slotTimes.get(3));
        matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, this.slotTimes.get(0));
        matchService.makeMatch(UserDto.from(users.get(1)), Option.BOTH, this.slotTimes.get(2));

        System.out.println("matchFindService.getAllMatchStatus(users.get(0).getId(), Option.BOTH) = "
                + matchFindService.getAllMatchStatus(UserDto.from(users.get(0)), Option.BOTH).getMatchBoards().get(0));
        System.out.println("matchFindService = " + matchFindService.getCurrentMatch(UserDto.from(users.get(0))));
        System.out.println("matchFindService.getCurrentMatch(UserDto.from(users.get(1))) = " + matchFindService.getCurrentMatch(UserDto.from(users.get(1))));
        System.out.println("matchFindService.getAllMatchStatus(users.get(1).getId(), Option.BOTH) = " + matchFindService.getAllMatchStatus(UserDto.from(users.get(1)), Option.BOTH));
        Optional<Game> game1 = gameRepository.findByStartTime(slotTimes.get(2));
        Assertions.assertThat(game1).isPresent();
        matchService.cancelMatch(UserDto.from(users.get(0)), slotTimes.get(2));
        System.out.println("matchFindService.getCurrentMatch(UserDto.from(users.get(1))) = " + matchFindService.getCurrentMatch(UserDto.from(users.get(1))));
        redisTemplate.delete(PenaltyKey.USER_ADMIN + users.get(0).getIntraId());
        matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, slotTimes.get(2));
        System.out.println("matchFindService = " + matchFindService.getCurrentMatch(UserDto.from(users.get(0))));
    }

    @DisplayName("매칭 경기 상대가 아닌 다른 유저의 매칭 경기 취소")
    @Test
    void cancelByNotMatchedUser() {
        System.out.println("this.users = " + this.users);
        // 유저 0 slot 2, 3, 0 등록 유저 1 slot 2
        matchService.makeMatch(UserDto.from(users.get(0)), Option.BOTH, this.slotTimes.get(2));
        matchService.makeMatch(UserDto.from(users.get(1)), Option.BOTH, this.slotTimes.get(2));
        org.junit.jupiter.api.Assertions.assertThrows(SlotNotFoundException.class, () -> {
            matchService.cancelMatch(UserDto.from(users.get(2)), this.slotTimes.get(2));
        });
    }

}
