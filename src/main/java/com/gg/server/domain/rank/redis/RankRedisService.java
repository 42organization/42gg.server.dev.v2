package com.gg.server.domain.rank.redis;

import com.gg.server.domain.season.data.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankRedisService {
    private final RankRedisRepository rankRedisRepository;

}
