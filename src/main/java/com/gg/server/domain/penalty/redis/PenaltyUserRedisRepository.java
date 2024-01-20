package com.gg.server.domain.penalty.redis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gg.server.admin.penalty.type.PenaltyKey;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PenaltyUserRedisRepository {
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
}
