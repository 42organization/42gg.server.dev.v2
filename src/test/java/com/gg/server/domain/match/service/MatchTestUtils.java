package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchTestUtils {
    private final UserRepository userRepository;
//    private final GameRepository gameRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final RankRedisRepository rankRedisRepository;
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SlotManagementRepository slotManagementRepository;

    public User createUser() {
        String randomId = UUID.randomUUID().toString().substring(0, 30);
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .imageUri("image")
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    public RankRedis addUsertoRankRedis(Long userId, Integer ppp, Long seasonId) {
        String randomId = UUID.randomUUID().toString();
        RankRedis rankRedis = new RankRedis(userId,  randomId, ppp, 0, 0,"test");
        rankRedisRepository.addRankData(RedisKeyManager.getHashKey(seasonId), userId, rankRedis);
        rankRedisRepository.addToZSet(RedisKeyManager.getZSetKey(seasonId), userId, ppp);
        return rankRedis;
    }

    public List<LocalDateTime> getTestSlotTimes(Integer interval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standard = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth()
        , now.getHour(), 0);
        List<LocalDateTime> sampleSlots = new ArrayList<LocalDateTime>();
        for (int i = 0; i < 15; i++) {
            if (standard.plusMinutes(interval * i).isAfter(now)) {
                sampleSlots.add(standard.plusMinutes(interval * i));
            }
        }
        return sampleSlots;
    }
    public Season makeTestSeason(Integer pppGap) {
        Season season = new Season(
                "test",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.of(9999, 12, 31, 23, 59, 59),
                1000,
                pppGap
        );
        seasonRepository.save(season);
        return season;
    }

    public SlotManagement makeTestSlotManagement(Integer interval) {
        SlotManagement slotManagement = SlotManagement.builder()
                .futureSlotTime(22)
                .pastSlotTime(2)
                .gameInterval(interval)
                .openMinute(5)
                .build();
        slotManagementRepository.save(slotManagement);
        return slotManagement;
    }
}
