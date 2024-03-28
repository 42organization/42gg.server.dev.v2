package gg.pingpong.api.user.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.rank.redis.RankRedis;
import gg.data.pingpong.season.Season;
import gg.data.user.User;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.user.UserRepository;
import gg.utils.RedisKeyManager;
import gg.utils.exception.rank.RedisDataNotFoundException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFindService {
	private final UserRepository userRepository;
	private final SeasonFindService seasonFindService;
	private final RankRedisRepository rankRedisRepository;

	@Transactional(readOnly = true)
	public User findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public User findByIntraId(String intraId) {
		return userRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public String getUserStatusMessage(User targetUser) {
		Season currentSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
		String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
		try {
			RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, targetUser.getId());
			return userRank.getStatusMessage();
		} catch (RedisDataNotFoundException e) {
			return "";
		}
	}
}
