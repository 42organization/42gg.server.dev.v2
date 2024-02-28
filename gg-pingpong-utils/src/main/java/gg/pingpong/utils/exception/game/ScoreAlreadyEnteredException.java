package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class ScoreAlreadyEnteredException extends DuplicationException {
	public ScoreAlreadyEnteredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
