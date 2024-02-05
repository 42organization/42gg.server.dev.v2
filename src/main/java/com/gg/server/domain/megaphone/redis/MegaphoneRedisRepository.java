package com.gg.server.domain.megaphone.redis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gg.server.data.store.redis.MegaphoneRedis;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MegaphoneRedisRepository {
	private final RedisTemplate<String, MegaphoneRedis> redisTemplate;

	public void addMegaphone(MegaphoneRedis megaphoneRedis) {
		Duration duration = Duration.between(megaphoneRedis.getUsedAt(), megaphoneRedis.getUsedAt().plusDays(1));
		redisTemplate.opsForValue().set("megaphone" + megaphoneRedis.getId(), megaphoneRedis, duration);
	}

	public List<MegaphoneRedis> getAllMegaphone() {
		return redisTemplate.opsForValue().multiGet(redisTemplate.keys("megaphone*"));
	}

	public void deleteAllMegaphone() {
		redisTemplate.delete(redisTemplate.keys("megaphone*"));
	}

	public void deleteMegaphoneById(Long id) {
		redisTemplate.delete("megaphone" + id);
	}

}
