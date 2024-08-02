package gg.repo.agenda;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Auth42Token;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class Auth42TokenRedisRepository {
	private final RedisTemplate<String, Auth42Token> redisTemplate;
	private static final long TOKEN_EXPIRATION_TIME = 3600; // 42Access 토큰의 만료는 두시간이지만 한시간으로 설정

	public void save42Token(String intraId, Auth42Token token) {
		redisTemplate.opsForValue().set(intraId, token, TOKEN_EXPIRATION_TIME, TimeUnit.SECONDS);
	}

	public Optional<Auth42Token> findByIntraId(String intraId) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(intraId));
	}

	public void expire42Token(String intraId) {
		redisTemplate.expire(intraId, 0, TimeUnit.SECONDS);
	}
}
