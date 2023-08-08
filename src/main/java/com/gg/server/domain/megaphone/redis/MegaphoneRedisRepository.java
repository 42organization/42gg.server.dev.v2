package com.gg.server.domain.megaphone.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class MegaphoneRedisRepository {
    private final RedisTemplate<String, MegaphoneRedis> redisTemplate;

    public void addMegaphone(MegaphoneRedis megaphoneRedis) {
        Duration duration = Duration.between(megaphoneRedis.getUsedAt(), megaphoneRedis.getUsedAt());
        redisTemplate.opsForValue().set("megaphone" + megaphoneRedis.getId(), megaphoneRedis, duration);
    }

    public List<MegaphoneRedis> getAllMegaphone() {
        return redisTemplate.opsForValue().multiGet(redisTemplate.keys("megaphone*"));
    }

    public void deleteAllMegaphone() {
        redisTemplate.delete(redisTemplate.keys("megaphone*"));
    }
}
