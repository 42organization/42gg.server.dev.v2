package gg.pingpong.api.global.log.service;

import com.gg.server.global.log.domain.TraceStatus;

public interface LogTrace {
	TraceStatus begin(String message);

	void end(TraceStatus status);

	void exception(TraceStatus status, Exception ex);
}
