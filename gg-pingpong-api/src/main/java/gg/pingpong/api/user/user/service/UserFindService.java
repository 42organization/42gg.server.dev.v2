package gg.pingpong.api.user.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.data.rank.redis.RankRedis;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.exception.rank.RedisDataNotFoundException;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFindService {
	private final UserRepository userRepository;
	private final SeasonFindService seasonFindService;
	private final RankRedisRepository rankRedisRepository;

	/**
	 * userId를 통해 유저를 조회, 반환
	 *
	 * @throw UserNotFoundException
	 * @param userId
	 * @return User
	 */
	@Transactional(readOnly = true)
	public User findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}

	/**
	 * intraId를 통해 유저를 조회, 반환
	 *
	 * @throw UserNotFoundException
	 * @param intraId
	 * @return User
	 */
	@Transactional(readOnly = true)
	public User findByIntraId(String intraId) {
		return userRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
	}

	/**
	 * 유저 정보가 랭크 데이터에 존재할 경우 상태메시지 반환, 없을 경우 빈 문자열 반환
	 *
	 * @param targetUser
	 * @return statusMessage
	 */
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
