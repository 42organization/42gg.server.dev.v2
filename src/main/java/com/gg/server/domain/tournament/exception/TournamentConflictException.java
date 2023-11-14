package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentConflictException extends DuplicationException {
    public TournamentConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
