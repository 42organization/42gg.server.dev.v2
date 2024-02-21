package gg.pingpong.api.global.log.service;

import org.springframework.stereotype.Component;

import gg.pingpong.api.global.log.domain.TraceId;
import gg.pingpong.api.global.log.domain.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThreadLocalLogTrace implements LogTrace {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

	public TraceStatus begin(String message) {
		syncTraceId();
		TraceId logId = traceIdHolder.get();
		Long startTimeMs = System.currentTimeMillis();
		log.info("[{}] {}{}", logId.getId(), addSpace(START_PREFIX, logId.getLevel()), message);

		return new TraceStatus(logId, startTimeMs, message);
	}

	public void end(TraceStatus status) {
		complete(status, null);
	}

	@Override
	public void exception(TraceStatus status, Exception exception) {
		if (status != null) {
			complete(status, exception);
		}
	}

	private void complete(TraceStatus status, Exception exception) {
		Long stopTimeMs = System.currentTimeMillis();
		long resultTimeMs = stopTimeMs - status.getStartTimeMs();
		TraceId traceId = status.getTraceId();
		if (exception == null) {
			log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()),
				status.getMessage(), resultTimeMs);
		} else {
			log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()),
				status.getMessage(), resultTimeMs, exception.toString());
		}

		releaseTraceId();
	}

	private void syncTraceId() {
		TraceId traceId = traceIdHolder.get();
		if (traceId == null) {
			traceIdHolder.set(new TraceId());
		} else {
			traceIdHolder.set(traceId.createNextId());
		}
	}

	private void releaseTraceId() {
		TraceId traceId = traceIdHolder.get();
		if (traceId.isFirstLevel()) {
			traceIdHolder.remove();
		} else {
			traceIdHolder.set(traceId.createPreviousId());
		}
	}

	private static String addSpace(String prefix, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append((i == level - 1) ? "|" + prefix : "|   ");
		}
		return sb.toString();
	}
}
