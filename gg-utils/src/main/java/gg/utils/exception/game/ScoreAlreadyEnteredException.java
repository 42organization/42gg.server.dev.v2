package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class ScoreAlreadyEnteredException extends DuplicationException {
	public ScoreAlreadyEnteredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
