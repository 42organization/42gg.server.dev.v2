package gg.pingpong.api.user.game.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.noti.Noti;
import gg.data.noti.type.NotiType;
import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.type.StatusType;
import gg.data.pingpong.manage.SlotManagement;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.noti.service.SnsNotiService;
import gg.pingpong.api.user.user.service.UserService;
import gg.repo.game.GameRepository;
import gg.repo.game.out.GameUser;
import gg.repo.manage.SlotManagementRepository;
import gg.utils.exception.game.GameDataConsistencyException;
import gg.utils.exception.match.SlotNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	@Caching(evict = {
		@CacheEvict(value = "allGameList", allEntries = true),
	})
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

	void cacheDelete() {
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "allGameList", allEntries = true),
	})
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
			log.error("imminent game size is not 2 -> size: " + games.size() + ", check time: " + getTime(
				slotManagement.getOpenMinute()));
			throw new GameDataConsistencyException();
		} else if (games.isEmpty()) {
			log.info("시작 " + slotManagement.getOpenMinute() + "분 전인 게임이 존재하지 않습니다.");
			return;
		} else {
			notiProcess(games.get(0), games.get(1).getIntraId(), slotManagement.getOpenMinute());
			notiProcess(games.get(1), games.get(0).getIntraId(), slotManagement.getOpenMinute());
		}
	}

	/**
	 * private method
	 */
	private void notiProcess(GameUser game, String enemyIntra, Integer gameOpenMinute) {
		Noti noti = notiService.createImminentNoti(userService.getUser(game.getUserId()), enemyIntra, NotiType.IMMINENT,
			gameOpenMinute);
		snsNotiService.sendSnsNotification(noti, new UserNotiDto(game));
	}

	private LocalDateTime getTime(int plusMiniute) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
			now.getHour(), now.getMinute(), 10);
		return endTime.plusMinutes(plusMiniute);
	}
}
