package gg.pingpong.api.user.rank.redis;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.data.game.redis.RankRedis;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.season.SeasonRepository;
import gg.pingpong.utils.RedisKeyManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisUploadService {
	private final RankRedisRepository redisRepository;
	private final SeasonRepository seasonRepository;
	private final RankRepository rankRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@PostConstruct
	@Transactional
	public void uploadRedis() {
		seasonRepository.findCurrentSeason(LocalDateTime.now()).ifPresent(currentSeason -> {
			String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
			if (redisRepository.isEmpty(hashKey)) {
				upload();
			}
		});
	}

	private void upload() {

		redisTemplate.executePipelined((RedisCallback<Object>)connection -> {
			seasonRepository.findAll().forEach(season -> {
				String hashKey = RedisKeyManager.getHashKey(season.getId());
				String zSetKey = RedisKeyManager.getZSetKey(season.getId());
				rankRepository.findAllBySeasonId(season.getId()).forEach(rank -> {
					RankRedis rankRedis = RankRedis.from(rank);
					connection.hSet(Objects.requireNonNull(keySerializer().serialize(hashKey)),
						Objects.requireNonNull(hashKeySerializer().serialize(rank.getUser().getId().toString())),
						Objects.requireNonNull(hashValueSerializer().serialize(rankRedis)));
					if (rank.getWins() + rankRedis.getLosses() != 0) {
						connection.zAdd(Objects.requireNonNull(keySerializer().serialize(zSetKey)), rank.getPpp(),
							Objects.requireNonNull(valueSerializer().serialize(rank.getUser().getId().toString())));
					}
				});
			});
			return null;
		});
	}

	private RedisSerializer valueSerializer() {
		return redisTemplate.getValueSerializer();
	}

	private RedisSerializer hashValueSerializer() {
		return redisTemplate.getHashValueSerializer();
	}

	private RedisSerializer hashKeySerializer() {
		return redisTemplate.getHashKeySerializer();
	}

	private RedisSerializer keySerializer() {
		return redisTemplate.getKeySerializer();
	}

}
