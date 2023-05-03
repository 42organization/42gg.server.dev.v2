package com.gg.server.domain.game.data;

import java.util.Optional;

public interface GameRepositoryCustom {
    Optional<Game> getLatestGameByUser(Long userId);
}
