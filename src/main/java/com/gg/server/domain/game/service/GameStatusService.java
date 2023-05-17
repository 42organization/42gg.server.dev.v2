package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameStatusService {

    private final GameRepository gameRepository;

    @Transactional
    public void updateBeforeToLiveStatus() {
        // game before 중에 현재 시작 시간인 경우 LIVE로 update
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute());
        List<Game> game = gameRepository.findAllByStatusAndStartTimeLessThanEqual(StatusType.BEFORE, startTime);
        for (Game g :
                game) {
            g.updateStatus();
        }
    }

    @Transactional
    public void updateLiveToWaitStatus() {
        // game before 중에 현재 시작 시간인 경우 LIVE로 update
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute() + 1);
        Optional<Game> game = gameRepository.findByStatusAndEndTimeLessThanEqual(StatusType.LIVE, endTime);
        game.ifPresent(Game::updateStatus);
    }
}
