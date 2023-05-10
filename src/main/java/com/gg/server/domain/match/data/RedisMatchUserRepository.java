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
    public void addMatchTime(Long userId, RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().add(MatchKey.USER.getCode() + userId, matchTime, getOrder(matchTime.getStartTime()));
    }

    public void deleteMatchUser(Long userId) {
        redisTemplate.delete(MatchKey.USER.getCode() + userId.toString());
    }
    public void deleteMatchTime(Long userId, RedisMatchTime matchTime) {
        redisTemplate.opsForZSet().remove(MatchKey.USER.getCode() + userId, matchTime);
    }

    public void pop(Long userId) {
        redisTemplate.opsForZSet().removeRange(MatchKey.USER.getCode() + userId.toString(), 0, 0);
    }

    public Long countMatchTime(Long userId) {
        return redisTemplate.opsForZSet().zCard(MatchKey.USER.getCode() + userId);
    }

    public Set<RedisMatchTime> getAllMatchTime(Long userId){
        return redisTemplate.opsForZSet().range(MatchKey.USER.getCode() + userId, 0, -1);
        //0 -1 idx 확인
    }

    public Double getMatchUserOrder(Long userId, RedisMatchTime matchTime) {
        return redisTemplate.opsForZSet().score(MatchKey.USER.getCode() + userId, matchTime);
    }
    //정렬 기준
    private double getOrder(LocalDateTime dateTime) {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        return (double) instant.toEpochMilli() / 1000.0;
    }
}
