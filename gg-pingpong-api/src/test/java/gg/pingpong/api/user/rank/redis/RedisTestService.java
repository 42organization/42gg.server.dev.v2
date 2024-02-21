package gg.pingpong.api.user.rank.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisTestService {
	private final RedisTemplate<String, Object> redisTemplate;

	public void addStringWithError(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
		throw new RuntimeException();
	}

	public void addString(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public Object getFromString(String key) {
		return redisTemplate.opsForValue().get(key);
	}
}
