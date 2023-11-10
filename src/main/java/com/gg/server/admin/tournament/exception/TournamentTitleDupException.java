package com.gg.server.admin.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentDupException extends DuplicationException {
    public TournamentDupException() {
        super(ErrorCode.TOURNAMENT_DUPLICATE.getMessage(), ErrorCode.TOURNAMENT_DUPLICATE);
    }
}
