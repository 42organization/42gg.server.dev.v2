package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TournamentNotFoundException extends NotExistException {
    public TournamentNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
