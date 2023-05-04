package com.gg.server.domain.match.data;

import com.gg.server.domain.match.type.MatchKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisMatchTimeRepository {
    private final RedisTemplate<String, RedisMatchUser> redisTemplate;

    public void addMatchUser(String matchTime, RedisMatchUser redisMatchUser) {
        redisTemplate.opsForList().rightPush(MatchKey.TIME.getCode() + matchTime, redisMatchUser);
    }
    public List<RedisMatchUser> getAllMatchUsers(String matchTime) {

        ListOperations<String, RedisMatchUser> listOperations = redisTemplate.opsForList();
        return listOperations.range(MatchKey.TIME.getCode() + matchTime, 0, - 1);
        //0, -1 idx 확인
    }

    public void setMatchTimeWithExpiry(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, startTime);
        redisTemplate.expire(MatchKey.TIME.getCode() + startTime.toString(), duration.getSeconds(), TimeUnit.SECONDS);
    }

    public void deleteMatchTime(String matchTime) {//매칭이 되거나 시간이 지나면 key를 지워준다.
        redisTemplate.delete(MatchKey.TIME.getCode() + matchTime);
    }

    public void deleteMatchUser(String matchTime, RedisMatchUser matchUser) {
        redisTemplate.opsForList().remove(MatchKey.TIME.getCode() + matchTime,0, matchUser);
    }
}
