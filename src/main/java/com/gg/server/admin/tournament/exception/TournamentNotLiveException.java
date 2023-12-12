package com.gg.server.admin.tournament.exception;


import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class TournamentNotLiveException extends CustomRuntimeException {
    public TournamentNotLiveException() {
        super(ErrorCode.TOURNAMENT_NOT_LIVE.getMessage(), ErrorCode.TOURNAMENT_NOT_LIVE);
    }
}
