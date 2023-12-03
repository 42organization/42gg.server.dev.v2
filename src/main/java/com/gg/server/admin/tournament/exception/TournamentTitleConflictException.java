package com.gg.server.admin.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentTitleConflictException extends DuplicationException {
    public TournamentTitleConflictException() {
        super(ErrorCode.TOURNAMENT_TITLE_CONFLICT.getMessage(), ErrorCode.TOURNAMENT_TITLE_CONFLICT);
    }
}
