package com.gg.server.admin.rank.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gg.server.domain.user.type.RoleType.GUEST;

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
        String tierImageUri = tierRepository.findAll().get(0).getImageUri();

        users.forEach(user -> {
            if (user.getRoleType() != GUEST) {
                UserDto userDto = UserDto.from(user);
                RankRedis userRank = RankRedis.from(userDto, seasonAdminDto.getStartPpp(), tierImageUri);

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
        if (userPlayedRank(userRank)){
            rankRedisRepository.deleteFromZSet(zsetKey, userId);
            rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
        }
    }

    private boolean userPlayedRank(RankRedis userRank) {
        return (userRank.getWins() + userRank.getLosses()) != 0;
    }
}
