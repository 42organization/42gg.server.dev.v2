package gg.pingpong.api.global.scheduler;

import org.springframework.stereotype.Component;

import gg.calendar.api.user.utils.service.FortyTwoEventService;
import gg.calendar.api.user.utils.service.ScheduleCheckService;
import gg.calendar.api.user.utils.service.ScheduleNotiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CalendarEventScheduler extends AbstractScheduler {
	private final FortyTwoEventService fortyTwoEventService;
	private final ScheduleCheckService scheduleCheckService;
	private final ScheduleNotiService scheduleNotiService;

	public CalendarEventScheduler(FortyTwoEventService fortyTwoEventService,
		ScheduleCheckService scheduleCheckService, ScheduleNotiService scheduleNotiService) {
		this.fortyTwoEventService = fortyTwoEventService;
		this.scheduleCheckService = scheduleCheckService;
		this.scheduleNotiService = scheduleNotiService;
		this.setCron("0 0 0 * * *");
	}

	@Override
	public Runnable runnable() {
		return () -> {
			log.info("FortyTwo Event Scheduler Started");
			fortyTwoEventService.checkEvent();
			log.info("Schedule check service deactivate Expired Schedules!");
			scheduleCheckService.deactivateExpiredSchedules();
			scheduleNotiService.sendScheduleNotifications();
		};
	}
}
