package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ForbiddenException;

public class TournamentUpdateException extends ForbiddenException {
    public TournamentUpdateException() {
        super(ErrorCode.TOURNAMENT_CAN_NOT_UPDATE.getMessage(), ErrorCode.TOURNAMENT_CAN_NOT_UPDATE);
    }
    public TournamentUpdateException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
