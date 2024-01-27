package com.gg.server.domain.season.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ForbiddenException;

public class SeasonForbiddenException extends ForbiddenException {
	public SeasonForbiddenException() {
		super(ErrorCode.SEASON_FORBIDDEN.getMessage(), ErrorCode.SEASON_FORBIDDEN);
	}
}
