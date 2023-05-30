package com.gg.server.global.log.domain;

import java.util.UUID;

public class LogId {
    private String id;
    private int level;

    public LogId() {
        this.id = createId();
        this.level = 0;
    }

    private LogId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public LogId createNextId() {
        return new LogId(id, level + 1);
    }

    public LogId createPreviousId() {
        return new LogId(id, level - 1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
