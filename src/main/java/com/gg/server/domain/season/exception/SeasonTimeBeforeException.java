package com.gg.server.domain.season.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ForbiddenException;

public class SeasonTimeBeforeException extends ForbiddenException {
    public SeasonTimeBeforeException() {
        super(ErrorCode.SEASON_TIME_BEFORE.getMessage(), ErrorCode.SEASON_TIME_BEFORE);
    }
}
