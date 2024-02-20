package gg.pingpong.repo.match;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gg.server.data.match.RedisMatchTime;
import com.gg.server.data.match.type.MatchKey;
import com.gg.server.data.match.type.Option;
import com.gg.server.domain.match.exception.PastSlotException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisMatchUserRepository {
	private final RedisTemplate<String, RedisMatchTime> redisTemplate;

	/**
	 * key : userId : startTime
	 * value : startTime
	 * slot의 startTime 각각의 만료기한 설정
	 * **/
	public void addMatchTime(Long userId, LocalDateTime startTime, Option option) {
		Duration duration = Duration.between(LocalDateTime.now(), startTime);
		if (duration.isNegative()) {
			throw new PastSlotException();
		}
		redisTemplate.opsForValue().set(MatchKey.getUserTime(userId, startTime),
			new RedisMatchTime(startTime, option), duration.getSeconds(), TimeUnit.SECONDS);

	}

	public void deleteMatchUser(Long userId) {
		Set<String> keys = redisTemplate.keys(MatchKey.getUser(userId) + "*");
		keys.stream().forEach(key -> redisTemplate.delete(key));
	}

	public void deleteMatchTime(Long userId, LocalDateTime startTime) {
		redisTemplate.delete(MatchKey.getUserTime(userId, startTime));
	}

	public int countMatchTime(Long userId) {
		return redisTemplate.keys(MatchKey.getUser(userId) + "*").size();
	}

	public Set<RedisMatchTime> getAllMatchTime(Long userId) {
		Set<String> keys = redisTemplate.keys(MatchKey.getUser(userId) + "*");
		return keys.stream().map(key -> redisTemplate.opsForValue().get(key)).collect(Collectors.toSet());
	}

	public Optional<RedisMatchTime> getUserTime(Long userId, LocalDateTime startTime) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(MatchKey.getUserTime(userId, startTime)));
	}
}
