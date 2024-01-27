package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class ScoreNotInvalidException extends InvalidParameterException {
	public ScoreNotInvalidException() {
		super(ErrorCode.SCORE_NOT_INVALID.getMessage(), ErrorCode.SCORE_NOT_INVALID);
	}
}
