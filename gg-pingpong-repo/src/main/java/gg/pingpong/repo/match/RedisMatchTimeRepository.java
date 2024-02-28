package gg.pingpong.repo.match;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import gg.pingpong.data.match.RedisMatchUser;
import gg.pingpong.data.match.type.MatchKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisMatchTimeRepository {
	private final RedisTemplate<String, RedisMatchUser> redisTemplate;

	public void addMatchUser(LocalDateTime startTime, RedisMatchUser redisMatchUser) {
		redisTemplate.opsForList().rightPush(MatchKey.getTime(startTime), redisMatchUser);
	}

	public List<RedisMatchUser> getAllMatchUsers(LocalDateTime startTime) {
		ListOperations<String, RedisMatchUser> listOperations = redisTemplate.opsForList();
		return listOperations.range(MatchKey.getTime(startTime), 0, -1);
	}

	public void setMatchTimeWithExpiry(LocalDateTime startTime) {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(now, startTime);
		redisTemplate.expire(MatchKey.getTime(startTime), duration.getSeconds(), TimeUnit.SECONDS);
	}

	public void deleteMatchTime(LocalDateTime startTime) { //매칭이 되거나 시간이 지나면 key를 지워준다.
		redisTemplate.delete(MatchKey.getTime(startTime));
	}

	public void deleteMatchUser(LocalDateTime startTime, RedisMatchUser matchUser) {
		redisTemplate.opsForList().remove(MatchKey.getTime(startTime), 0, matchUser);
	}

	public Set<LocalDateTime> getAllEnrolledStartTimes() {
		Set<String> keys = redisTemplate.keys(MatchKey.getAllTime() + "*");
		Integer prefixIdx = MatchKey.getAllTime().length();
		return keys.stream().map(str -> LocalDateTime.parse(str.substring(prefixIdx)))
			.collect(Collectors.toSet());
	}

}
