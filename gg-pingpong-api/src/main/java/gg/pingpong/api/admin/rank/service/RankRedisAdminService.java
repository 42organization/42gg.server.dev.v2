package gg.pingpong.api.admin.rank.service;

import static gg.data.user.type.RoleType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.rank.Tier;
import gg.data.pingpong.rank.redis.RankRedis;
import gg.data.user.User;
import gg.pingpong.api.admin.season.dto.SeasonAdminDto;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.user.UserRepository;
import gg.utils.RedisKeyManager;
import gg.utils.exception.tier.TierNotFoundException;
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
				RankRedis userRank = RankRedis.from(user.getId(), user.getIntraId(), user.getTextColor(),
					seasonAdminDto.getStartPpp(), tier.getImageUri());

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
