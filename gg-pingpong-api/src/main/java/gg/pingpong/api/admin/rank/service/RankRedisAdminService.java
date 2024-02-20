package gg.pingpong.api.admin.rank.service;

import static gg.pingpong.data.user.type.RoleType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.admin.season.dto.SeasonAdminDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.game.redis.RankRedis;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.tier.TierRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.exception.tier.TierNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RankRedisAdminService {
	private final UserRepository userRepository;
	private final RankRedisRepository rankRedisRepository;
	private final TierRepository tierRepository;

	@Transactional
	public void addAllUserRankByNewSeason(SeasonAdminDto seasonAdminDto) {
		List<User> users = userRepository.findAll();

		String redisHashKey = RedisKeyManager.getHashKey(seasonAdminDto.getSeasonId());
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);

		users.forEach(user -> {
			if (user.getRoleType() != GUEST) {
				UserDto userDto = UserDto.from(user);
				RankRedis userRank = RankRedis.from(userDto, seasonAdminDto.getStartPpp(), tier.getImageUri());

				rankRedisRepository.addRankData(redisHashKey, user.getId(), userRank);
			}
		});
	}

	@Transactional
	public void deleteSeasonRankBySeasonId(Long seasonId) {
		String redisHashKey = RedisKeyManager.getHashKey(seasonId);

		rankRedisRepository.deleteHashKey(redisHashKey);
	}

	public void updateRankUser(String hashKey, String zsetKey, Long userId, RankRedis userRank) {
		rankRedisRepository.updateRankData(hashKey, userId, userRank);
		if (userPlayedRank(userRank)) {
			rankRedisRepository.deleteFromZSet(zsetKey, userId);
			rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
		}
	}

	private boolean userPlayedRank(RankRedis userRank) {
		return (userRank.getWins() + userRank.getLosses()) != 0;
	}
}
