package gg.admin.repo.match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import gg.data.match.RedisMatchUser;
import gg.data.match.type.MatchKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisMatchTimeAdminRepository {
	private final RedisTemplate<String, RedisMatchUser> redisTemplate;

	public Map<LocalDateTime, List<RedisMatchUser>> getAllEnrolledSlots() {
		Set<String> keys = redisTemplate.keys(MatchKey.getAllTime() + "*");
		int prefixIdx = MatchKey.getAllTime().length();

		return keys.stream().collect(Collectors.toMap(
			key -> LocalDateTime.parse(key.substring(prefixIdx)),
			key -> redisTemplate.opsForList().range(key, 0, -1)));
	}
}
