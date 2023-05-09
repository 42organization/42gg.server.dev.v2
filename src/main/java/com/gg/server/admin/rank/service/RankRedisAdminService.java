package com.gg.server.admin.rank.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RankRedisAdminService {
    private final UserRepository userRepository;
    private final RankRedisRepository rankRedisRepository;

    @Transactional
    public void addAllUserRankByNewSeason(SeasonAdminDto seasonAdminDto) {
        List<User> users = userRepository.findAll();

        String redisZSetKey = RedisKeyManager.getZSetKey(seasonAdminDto.getSeasonId());
        String redisHashKey = RedisKeyManager.getHashKey(seasonAdminDto.getSeasonId());

        users.forEach(user -> {
            UserDto userDto = UserDto.from(user);
            RankRedis userRank = RankRedis.from(userDto, seasonAdminDto.getStartPpp());

            rankRedisRepository.addToZSet(redisZSetKey, user.getId(), seasonAdminDto.getStartPpp());
            rankRedisRepository.addRankData(redisHashKey, user.getId(), userRank);
        });
    }

}
