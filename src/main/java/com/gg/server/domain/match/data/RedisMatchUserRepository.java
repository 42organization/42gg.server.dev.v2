package com.gg.server.domain.match.data;

import com.gg.server.domain.match.type.MatchKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    public void addMatchTime(String nickName, RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().add(MatchKey.USER.getCode() + nickName, matchTime, getOrder(matchTime.getStartTime()));
    }
    public void deleteMatchTime(String nickName, RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().remove(MatchKey.USER.getCode() + nickName, matchTime);
    }

    public void pop(String nickName) {
        redisTemplate.opsForZSet().removeRange(MatchKey.USER.getCode() + nickName, 0, 0);
    }

    public Long countMatchTime(String nickName) {
        return redisTemplate.opsForZSet().zCard(MatchKey.USER.getCode() + nickName);
    }

    public Set<RedisMatchTime> getAllMatchTime(String nickName){
        return redisTemplate.opsForZSet().range(MatchKey.USER.getCode() + nickName, 0, -1);
        //0 -1 idx 확인
    }

    public Double getMatchUserOrder(String nickName, RedisMatchTime matchTime) {
        return redisTemplate.opsForZSet().score(MatchKey.USER.getCode() + nickName, matchTime);
    }
    //정렬 기준
    private double getOrder(LocalDateTime dateTime) {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        return (double) instant.toEpochMilli() / 1000.0;
    }
}
