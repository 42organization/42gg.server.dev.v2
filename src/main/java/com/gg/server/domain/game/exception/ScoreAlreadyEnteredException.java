package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class ScoreAlreadyEnteredException extends DuplicationException {
	public ScoreAlreadyEnteredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
