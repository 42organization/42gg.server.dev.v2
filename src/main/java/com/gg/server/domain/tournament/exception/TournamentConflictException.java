package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentConflictException extends DuplicationException {
    public TournamentConflictException() {
        super(ErrorCode.TOURNAMENT_CONFLICT.getMessage(), ErrorCode.TOURNAMENT_CONFLICT);
    }
    public TournamentConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage(), ErrorCode.TOURNAMENT_CONFLICT);
    }
}
