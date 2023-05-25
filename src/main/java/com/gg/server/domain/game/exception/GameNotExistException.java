package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class GameNotExistException extends NotExistException {
    public GameNotExistException() {
        super("game 이 존재하지 않습니다.", ErrorCode.GAME_NOT_FOUND);
    }
}
