package com.gg.server.admin.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class NotRecentlyGameException extends InvalidParameterException {
    public NotRecentlyGameException() {
        super(ErrorCode.GAME_NOT_RECENTLY.getMessage(), ErrorCode.GAME_NOT_RECENTLY);
    }
}
