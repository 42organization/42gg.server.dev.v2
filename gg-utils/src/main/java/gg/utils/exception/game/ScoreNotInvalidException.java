package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class ScoreNotInvalidException extends InvalidParameterException {
	public ScoreNotInvalidException() {
		super(ErrorCode.SCORE_NOT_INVALID.getMessage(), ErrorCode.SCORE_NOT_INVALID);
	}
}
