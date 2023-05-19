package com.gg.server.admin.penalty.data;

import com.gg.server.admin.penalty.type.PenaltyKey;
import com.gg.server.domain.penalty.data.RedisPenaltyUser;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisPenaltyUserRepository {
    private final RedisTemplate<String, RedisPenaltyUser> redisTemplate;

    public void addPenaltyUser(RedisPenaltyUser penaltyUser, LocalDateTime releaseTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, releaseTime);
        redisTemplate.opsForValue().set(PenaltyKey.USER_ADMIN + penaltyUser.getIntraId(), penaltyUser,
                duration.getSeconds(), TimeUnit.SECONDS);
    }
    public Optional<RedisPenaltyUser> findByIntraId(String intraId) {
        RedisPenaltyUser penaltyUser = redisTemplate.opsForValue().get(PenaltyKey.USER_ADMIN + intraId);
        return Optional.ofNullable(penaltyUser);
    }


    public List<RedisPenaltyUser> findAll() {
        Set<String> penaltyUserKeys = redisTemplate.keys(PenaltyKey.ALL + "*");
        List<RedisPenaltyUser> users = penaltyUserKeys.stream().map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());
        return users;
    }

    public List<RedisPenaltyUser> findAllByKeyword(String keyword) {
        Set<String> penaltyUserKeys = redisTemplate.keys(PenaltyKey.ALL + "*" + keyword + "*");
        List<RedisPenaltyUser> users = penaltyUserKeys.stream().map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());
        return users;
    }
    public void deletePenaltyUser(String intraId) {
        redisTemplate.delete(PenaltyKey.USER_ADMIN + intraId);
    }
}
