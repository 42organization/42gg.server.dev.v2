package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class ScoreNotMatchedException extends InvalidParameterException{
    public ScoreNotMatchedException() {
        super(ErrorCode.SCORE_NOT_MATCHED.getMessage(), ErrorCode.SCORE_NOT_MATCHED);
    }
}
