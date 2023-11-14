package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentOverlapException extends DuplicationException {
    public TournamentOverlapException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
