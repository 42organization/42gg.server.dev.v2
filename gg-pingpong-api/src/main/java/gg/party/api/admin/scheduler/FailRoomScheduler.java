package gg.party.api.admin.scheduler;

import org.springframework.stereotype.Component;

import gg.party.api.admin.room.service.RoomStatusService;
import gg.pingpong.api.global.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FailRoomScheduler extends AbstractScheduler {
	private final RoomStatusService roomStatusService;

	public FailRoomScheduler(RoomStatusService roomStatusService) {
		this.roomStatusService = roomStatusService;
		this.setCron("0 0/5 * * * *");
	}

	@Override
	public Runnable runnable() {
		return () -> {
			log.info("FailOpenedRoomsScheduler start");
			roomStatusService.failOpenedRooms();
		};
	}
}
