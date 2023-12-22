package com.gg.server.domain.team.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TeamNotFoundException extends NotExistException {

    public TeamNotFoundException() {
        super(ErrorCode.TEAM_NOT_FOUND.getMessage(), ErrorCode.TEAM_NOT_FOUND);
    }
}
