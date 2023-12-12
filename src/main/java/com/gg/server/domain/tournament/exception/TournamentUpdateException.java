package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class TournamentUpdateException extends CustomRuntimeException {

    public TournamentUpdateException() {
        super(ErrorCode.TOURNAMENT_CANT_UPDATE.getMessage(), ErrorCode.TOURNAMENT_CANT_UPDATE);
    }
    public TournamentUpdateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
