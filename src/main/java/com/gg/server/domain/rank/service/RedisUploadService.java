package com.gg.server.domain.rank.service;

import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedisUploadService {
    private final RankRedisRepository redisRepository;
    private final SeasonRepository seasonRepository;
    private final RankRepository rankRepository;

    @PostConstruct
    @Transactional
    public void uploadRedis() {
        Season currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new SeasonNotFoundException("현재 시즌이 없습니다.", ErrorCode.SEASON_NOT_FOUND));
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
        if(redisRepository.isEmpty(hashKey))
            upload();
    }

    private void upload() {
        seasonRepository.findAll().forEach(season -> {
            String hashKey = RedisKeyManager.getHashKey(season.getId());
            String zSetKey = RedisKeyManager.getZSetKey(season.getId());
            rankRepository.findAllBySeasonId(season.getId()).forEach(rank -> {
                RankRedis rankRedis = RankRedis.from(rank);
                redisRepository.addRankData(hashKey, rank.getUser().getId(), rankRedis);
                if (rank.getWins() + rankRedis.getLosses() != 0)
                    redisRepository.addToZSet(zSetKey, rank.getUser().getId(), rank.getPpp());
            });
        });
    }
}
