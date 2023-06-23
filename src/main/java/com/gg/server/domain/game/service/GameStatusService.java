package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameDataConsistencyException;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.dto.GameUser;
import com.gg.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut.Slot;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    private final UserService userService;
    private final SlotManagementRepository slotManagementRepository;

    @Transactional
    public void updateBeforeToLiveStatus() {
        // game before 중에 현재 시작 시간인 경우 LIVE로 update
        List<Game> game = gameRepository.findAllByStatusAndStartTimeLessThanEqual(StatusType.BEFORE, getTime(0));
        if (!game.isEmpty()) {
            cacheDelete();
        }
        for (Game g : game) {
            g.updateStatus();
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "allGameList", allEntries = true),
    })
    void cacheDelete() {}

    @Transactional
    public void updateLiveToWaitStatus() {
        // game live 중에 종료 시간인 경우 wait 로 update
        LocalDateTime endTime = getTime(1);
        List<Game> game = gameRepository.findAllByStatusAndEndTimeLessThanEqual(StatusType.LIVE, endTime);
        if (!game.isEmpty()) {
            cacheDelete();
        }
        for (Game g : game) {
            g.updateStatus();
        }
    }

    @Transactional
    public void imminentGame() {
        SlotManagement slotManagement = slotManagementRepository.findCurrent(LocalDateTime.now())
                .orElseThrow(SlotNotFoundException::new);
        List<GameUser> games = gameRepository.findAllByStartTimeLessThanEqual(getTime(slotManagement.getOpenMinute()));
        if (games.size() > 2) {
            log.error("imminent game size is not 2 -> size: " + games.size() + ", check time: " + getTime(slotManagement.getOpenMinute()));
            throw new GameDataConsistencyException();
        } else if (games.isEmpty()) {
            log.info("시작 " + slotManagement.getOpenMinute() + "분 전인 게임이 존재하지 않습니다.");
            return;
        } else {
            notiProcess(games.get(0), games.get(1).getIntraId(), slotManagement.getOpenMinute());
            notiProcess(games.get(1), games.get(0).getIntraId(), slotManagement.getOpenMinute());
        }
    }

    private void notiProcess(GameUser game, String enemyIntra, Integer gameOpenMinute) {
        Noti noti = notiService.createImminentNoti(userService.getUser(game.getUserId()), enemyIntra, NotiType.IMMINENT, gameOpenMinute);
        snsNotiService.sendSnsNotification(noti, new UserNotiDto(game));
    }

    private LocalDateTime getTime(int plusMiniute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), 10);
        return endTime.plusMinutes(plusMiniute);
    }
}
