package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class GameNotFoundException extends NotExistException {
    public GameNotFoundException() {
        super(ErrorCode.GAME_NOT_FOUND.getMessage(), ErrorCode.GAME_NOT_FOUND);
    }
}
