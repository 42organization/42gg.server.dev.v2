package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TournamentNotFoundException extends NotExistException {
    public TournamentNotFoundException() {
        super(ErrorCode.TOURNAMENT_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_NOT_FOUND);
    }
    public TournamentNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), ErrorCode.TOURNAMENT_NOT_FOUND);
    }
}
