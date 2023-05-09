package com.gg.server.domain.match.data;

import com.gg.server.domain.match.type.MatchKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisMatchTimeRepository {
    private final RedisTemplate<String, RedisMatchUser> redisTemplate;

    public void addMatchUser(LocalDateTime startTime, RedisMatchUser redisMatchUser) {
        redisTemplate.opsForList().rightPush(MatchKey.TIME.getCode() + startTime.toString(), redisMatchUser);
    }
    public List<RedisMatchUser> getAllMatchUsers(LocalDateTime startTime) {
        ListOperations<String, RedisMatchUser> listOperations = redisTemplate.opsForList();
        return listOperations.range(MatchKey.TIME.getCode() + startTime.toString(), 0, - 1);
    }

    public void setMatchTimeWithExpiry(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, startTime);
        redisTemplate.expire(MatchKey.TIME.getCode() + startTime.toString(), duration.getSeconds(), TimeUnit.SECONDS);
    }

    public void deleteMatchTime(LocalDateTime startTime) {//매칭이 되거나 시간이 지나면 key를 지워준다.
        redisTemplate.delete(MatchKey.TIME.getCode() + startTime.toString());
    }

    public void deleteMatchUser(LocalDateTime startTime, RedisMatchUser matchUser) {
        redisTemplate.opsForList().remove(MatchKey.TIME.getCode() + startTime.toString(),0, matchUser);
    }

   public Optional<Set<LocalDateTime>> getAllEnrolledStartTimes() {
        Set<String> keys = redisTemplate.keys(MatchKey.TIME.getCode() + "*");
       Integer prefixIdx = MatchKey.TIME.getCode().length();
       Optional<Set<LocalDateTime>> times = keys.stream().map(str -> LocalDateTime.parse(str.substring(prefixIdx)))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Optional::ofNullable));
        return times;
    }

}
