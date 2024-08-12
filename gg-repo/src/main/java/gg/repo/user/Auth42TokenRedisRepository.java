package gg.repo.user;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Auth42Token;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Deprecated
@Repository
@RequiredArgsConstructor
public class Auth42TokenRedisRepository {
	private final RedisTemplate<String, Auth42Token> redisTemplate;
	private static final long TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60; // 7일

	public void save42Token(String intraId, Auth42Token token) {
		redisTemplate.opsForValue().set(intraId, token, TOKEN_EXPIRATION_TIME, TimeUnit.SECONDS);
	}

	public Optional<Auth42Token> findByIntraId(String intraId) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(intraId));
	}

	public void expire42Token(String intraId) {
		redisTemplate.expire(intraId, 0, TimeUnit.SECONDS);
	}

	public void update42Token(String intraId, Auth42Token token) {
		Long currentTtl = redisTemplate.getExpire(intraId, TimeUnit.SECONDS);

		if (currentTtl == null) {
			throw new NotExistException("토큰이 존재하지 않습니다.");
		}
		redisTemplate.opsForValue().set(intraId, token, currentTtl, TimeUnit.SECONDS);
	}
}
