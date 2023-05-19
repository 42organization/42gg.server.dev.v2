package com.gg.server.domain.pchange.service;

import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.rank.redis.RankRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PChangeService {
    private final GameService gameService;
    private final RankRedisService rankRedisService;
}
