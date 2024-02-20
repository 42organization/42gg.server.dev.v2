package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.InvalidParameterException;

public class ScoreNotInvalidException extends InvalidParameterException {
	public ScoreNotInvalidException() {
		super(ErrorCode.SCORE_NOT_INVALID.getMessage(), ErrorCode.SCORE_NOT_INVALID);
	}
}
