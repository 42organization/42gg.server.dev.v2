package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameDataConsistencyException;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.dto.GameUser;
import com.gg.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class GameStatusService {

    private final GameRepository gameRepository;
    private final SnsNotiService snsNotiService;
    private final NotiService notiService;
    private final TeamRepository teamRepository;
    private final UserService userService;

    @Transactional
    public void updateBeforeToLiveStatus() {
        // game before 중에 현재 시작 시간인 경우 LIVE로 update
        List<Game> game = gameRepository.findAllByStatusAndStartTimeLessThanEqual(StatusType.BEFORE, getTime(0));
        for (Game g : game) {
            g.updateStatus();
        }
    }

    @Transactional
    public void updateLiveToWaitStatus() {
        // game live 중에 현재 시작 시간인 경우 wait 로 update
        LocalDateTime endTime = getTime(1);
        List<Game> game = gameRepository.findAllByStatusAndEndTimeLessThanEqual(StatusType.LIVE, endTime);
        for (Game g : game) {
            g.updateStatus();
        }
    }

    @Transactional
    public void imminentGame() {
        List<GameUser> games = teamRepository.findAllByStartTimeEquals(getTime(5));
        if (games.size() > 2) {
            log.error("imminent game size is not 2 -> size: " + games.size() + ", check time: " + getTime(5));
            throw new GameDataConsistencyException();
        } else if (games.isEmpty()) {
            log.info("시작 5분 전인 게임이 존재하지 않습니다.");
            return;
        } else {
            notiProcess(games.get(0), games.get(1).getUserId());
            notiProcess(games.get(1), games.get(0).getUserId());
        }
    }

    private void notiProcess(GameUser game, Long enemyId) {
        Noti noti = notiService.createNoti(userService.getUser(game.getUserId()), enemyId.toString(), NotiType.IMMINENT);
        snsNotiService.sendSnsNotification(noti, new UserNotiDto(game));
    }

    private LocalDateTime getTime(int plusMiniute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), 1);
        return endTime.plusMinutes(plusMiniute);
    }
}
