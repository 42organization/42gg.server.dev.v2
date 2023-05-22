package com.gg.server.domain.pchange.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PChangeService {
    private final PChangeRepository pChangeRepository;

    @Transactional
    public void addPChange(Game game, User user, Integer pppResult) {
        pChangeRepository.save(new PChange(game, user, pppResult));
    }
}
