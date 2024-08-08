package gg.repo.user;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JwtRedisRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public void addRefToken(String refreshToken, long timeOut, Long userId) {
		redisTemplate.opsForValue().set(refreshToken, userId.toString(), timeOut, TimeUnit.MILLISECONDS);
	}

	public Long getUserIdFromRefToken(String refToken) {
		String userId = redisTemplate.opsForValue().get(refToken);
		if (userId == null) {
			return null;
		}
		return Long.valueOf(userId);
	}

	public void deleteRefToken(String refToken) {
		redisTemplate.delete(refToken);
	}
}
