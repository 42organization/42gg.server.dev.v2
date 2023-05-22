package com.gg.server.domain.season.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SeasonNotFoundException extends NotExistException {
    public SeasonNotFoundException() {
        super("시즌이 없습니다.", ErrorCode.SEASON_NOT_FOUND);
    }
}
