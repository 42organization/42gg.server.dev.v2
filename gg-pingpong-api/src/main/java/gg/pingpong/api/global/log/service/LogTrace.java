package gg.pingpong.api.global.log.service;

import gg.pingpong.api.global.log.domain.TraceStatus;

public interface LogTrace {
	TraceStatus begin(String message);

	void end(TraceStatus status);

	void exception(TraceStatus status, Exception ex);
}
