package com.gg.server.global.log.service;

import com.gg.server.global.log.domain.LogId;
import com.gg.server.global.log.domain.LogStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThreadLocalLogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<LogId> traceIdHolder = new ThreadLocal<>();

    public LogStatus begin(String message) {
        syncTraceId();
        LogId logId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", logId.getId(), addSpace(START_PREFIX, logId.getLevel()), message);

        return new LogStatus(logId, startTimeMs, message);
    }

    public void end(LogStatus status) {
        complete(status, null);
    }

    public void exception(LogStatus status, Exception e) {
        if (status != null) {
            complete(status, e);
        }
    }

    private void complete(LogStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        LogId traceId = status.getLogId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }

        releaseTraceId();
    }

    private void syncTraceId() {
        LogId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new LogId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private void releaseTraceId() {
        LogId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();//destroy
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
