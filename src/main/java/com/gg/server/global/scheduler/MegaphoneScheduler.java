package com.gg.server.global.scheduler;

import com.gg.server.domain.megaphone.service.MegaphoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
