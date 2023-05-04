package com.gg.server.domain.match.service;

import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.type.MatchKey;
import com.gg.server.domain.match.type.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
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

    List<String> intraIds;
    List<Integer> ppps;
    List<LocalDateTime> startTimes;
    List<Option> options;

    public MatchRedisServiceTest() {
        this.intraIds = List.of("yuikim3", "lllll", "yuikim", "abcd", "sdfsdf");;
        this.ppps = List.of(200, 320, 330, 310, 420);;
        LocalDateTime now = LocalDateTime.now();
        this.startTimes = List.of(now.plusMinutes(15),
                now.plusMinutes(30),
                now.plusMinutes(45),
                now.plusMinutes(60)
        );
        this.options = List.of(Option.BOTH, Option.NORMAL, Option.RANK, Option.BOTH, Option.RANK);;
    }

    @BeforeEach
    void init() {
        for (int i = 0; i < intraIds.size() ; i++) {
            matchRedisService.makeMatch(intraIds.get(i), ppps.get(i), options.get(i), startTimes.get(1));
            matchRedisService.makeMatch(intraIds.get(i), ppps.get(i), options.get(i), startTimes.get(0));
        }

    }
    @AfterEach
    void clear() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushDb();
        connection.close();
    }

    @DisplayName("경기 등록")
    @Test
    void addMatch() {
        Long matchTimeSize = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + startTimes.get(0).toString());
        Assertions.assertThat(matchTimeSize).isEqualTo(intraIds.size());
        Long matchUserSize;
        for (String nickName : intraIds) {
            matchUserSize = redisMatchUserRepository.countMatchTime(nickName);
            Assertions.assertThat(matchUserSize).isEqualTo(2);
        }
    }

    @DisplayName("경기 등록 취소 ")
    @Test
    void cancelMatch() {
        //in MATCH:TIME:*
        Long sizeBeforeCancel = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + startTimes.get(0).toString());
        //in MATCH:USER:*
        Long before = redisMatchUserRepository.countMatchTime("yuikim");
        matchRedisService.cancelMatch("yuikim", startTimes.get(0));
        Long sizeAfterCancel = redisTemplate.opsForList().size(MatchKey.TIME.getCode() + startTimes.get(0).toString());
        Long after = redisMatchUserRepository.countMatchTime("yuikim");
        Assertions.assertThat(sizeBeforeCancel - 1).isEqualTo(sizeAfterCancel);
        Assertions.assertThat(before -1).isEqualTo(after);
    }

    @DisplayName("경기 등록 취소 후 다시 경기 등록")
    @Test
    void cancelAndRetryMatch() {
        Long before = redisMatchUserRepository.countMatchTime("yuikim");
        matchRedisService.cancelMatch("yuikim", startTimes.get(0));
        Long afterCancel = redisMatchUserRepository.countMatchTime("yuikim");
        matchRedisService.makeMatch("yuikim", ppps.get(3), options.get(3), startTimes.get(0));
        Long afterMakeMatch = redisMatchUserRepository.countMatchTime("yuikim");
        Assertions.assertThat(before - 1).isEqualTo(afterCancel);
        Assertions.assertThat(afterMakeMatch).isEqualTo(afterCancel + 1);

    }
    @DisplayName("키 만료기한 확인")
    @Test
    void checkExpiry() {
        redisMatchTimeRepository.setMatchTimeWithExpiry(startTimes.get(0));
        Long expireDate = redisTemplate.getExpire(MatchKey.TIME.getCode() + startTimes.get(0), TimeUnit.MINUTES);
        Assertions.assertThat(expireDate).isBetween(14L, 15L);
    }

}
