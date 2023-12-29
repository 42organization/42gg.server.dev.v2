package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class LosingTeamNotFoundException extends NotExistException {
    public LosingTeamNotFoundException() {
        super(ErrorCode.LOSING_TEAM_NOT_FOUND.getMessage(), ErrorCode.LOSING_TEAM_NOT_FOUND);
    }
}
