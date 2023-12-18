package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class WinningTeamNotFoundException extends NotExistException {
    public WinningTeamNotFoundException() {
        super(ErrorCode.WINNING_TEAM_NOT_FOUND.getMessage(), ErrorCode.WINNING_TEAM_NOT_FOUND);
    }
}
