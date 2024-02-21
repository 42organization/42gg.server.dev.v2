package gg.pingpong.api.global.scheduler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import gg.pingpong.api.user.megaphone.service.MegaphoneService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MegaphoneScheduler extends AbstractScheduler {
	private final MegaphoneService megaphoneService;

	public MegaphoneScheduler(MegaphoneService megaphoneService) {
		this.megaphoneService = megaphoneService;
		this.setCron("0 59 23 * * *");
	}

	@Override
	public Runnable runnable() {
		return () -> {
			log.info("Set Megaphone List ");
			megaphoneService.setMegaphoneList(LocalDate.now());
		};
	}
}
