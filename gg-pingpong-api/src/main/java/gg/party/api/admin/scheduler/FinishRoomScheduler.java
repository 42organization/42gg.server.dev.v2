package gg.party.api.admin.scheduler;

import org.springframework.stereotype.Component;

import gg.party.api.admin.room.service.RoomStatusService;
import gg.pingpong.api.global.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FinishRoomScheduler extends AbstractScheduler {
	private final RoomStatusService roomStatusService;

	public FinishRoomScheduler(RoomStatusService roomStatusService) {
		this.roomStatusService = roomStatusService;
		this.setCron("0 0/5 * * * *"); // Example: Run every 5 minutes
	}

	@Override
	public Runnable runnable() {
		return () -> {
			log.info("FinishStartedRoomsScheduler start");
			roomStatusService.finishStartedRooms();
		};
	}
}
