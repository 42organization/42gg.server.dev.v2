package com.gg.server.global.scheduler;

import com.gg.server.domain.game.service.GameStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameStatusScheduler extends AbstractScheduler{
    private final GameStatusService gameStatusService;

    public GameStatusScheduler(GameStatusService gameStatusService) {
        this.gameStatusService = gameStatusService;
        this.setCron("0 */5 * * * *");
    }

    @Override
    public Runnable runnable() {
        return () -> {
            log.info("GameStatusScheduler start");
            // BEFORE -> LIVE
            gameStatusService.updateBeforeToLiveStatus();
            // LIVE -> WAIT
            gameStatusService.updateLiveToWaitStatus();
            // imminent Noti
            gameStatusService.imminentGame();
        };
    }
}
