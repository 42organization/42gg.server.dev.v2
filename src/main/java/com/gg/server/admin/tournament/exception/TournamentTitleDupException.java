package com.gg.server.admin.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class TournamentTitleDupException extends DuplicationException {
    public TournamentTitleDupException() {
        super(ErrorCode.TOURNAMENT_TITLE_DUPLICATE.getMessage(), ErrorCode.TOURNAMENT_TITLE_DUPLICATE);
    }
}
