package com.gg.server.domain.match.repository;

import com.gg.server.domain.match.RedisMatchTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisMatchUserRepository {
    private final RedisTemplate redisTemplate;
    //일단 sorted set으로 하고 시간이 지나거나 매칭이 되면 user queue
    //에서 빼주는 방식으로 구현
    public void addMatchTime(String key, RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().add(key, matchTime, getOrder(matchTime.getStartTime()));
    }
    public void deleteMatchTime(RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().removeRange(matchTime, 0, 0);
    }

    public Long countMatchTime(String key) { //userId로 할지 말지를 정해야 함
        return redisTemplate.opsForZSet().zCard(key);
    }

    public Set<RedisMatchTime> getAllMatchTime(String UserId){//login한 user 기준
        return redisTemplate.opsForZSet().range(UserId, 0, -1);
        //0 -1 idx 확인
    }

    private double getOrder(LocalDateTime dateTime) {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        //출력값 확인해볼 필요가 있음
        return (double) instant.toEpochMilli() / 1000.0;
    }
}
