package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUserInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
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
        // game before 중에 현재 시작 시간인 경우 LIVE로 update
        LocalDateTime endTime = getTime(1);
        List<Game> game = gameRepository.findAllByStatusAndEndTimeLessThanEqual(StatusType.LIVE, endTime);
        for (Game g : game) {
            g.updateStatus();
        }
    }

    @Transactional
    public void imminentGame() {
        List<GameUser> games = teamRepository.findAllByStartTimeEquals(getTime(5));
        for (GameUser gu :
                games) {
            Noti noti = notiService.createNoti(userService.getUser(gu.getUserId()), null, NotiType.IMMINENT);
            snsNotiService.sendSnsNotification(noti, new UserNotiDto(gu));
        }
    }
    private LocalDateTime getTime(int plusMiniute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute());
        return endTime.plusMinutes(plusMiniute);
    }
}
