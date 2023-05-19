package com.gg.server.global.scheduler;

import lombok.Getter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractScheduler {
    private ThreadPoolTaskScheduler scheduler;
    @Getter
    protected String cron;
    @Getter
    protected Integer interval;

    public abstract Runnable runnable();

    public void renewScheduler() {
        scheduler.shutdown();
        startScheduler();
    }

    protected void setCron(String cron) {
        this.cron = cron;
    }

    protected void setInterval(Integer interval) {
        this.interval = interval;
    }

    @PostConstruct
    public void init() {
        startScheduler();
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
    }

    private void startScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.initialize();
        scheduler.schedule(this.runnable(), new CronTrigger(cron));
    }
}
