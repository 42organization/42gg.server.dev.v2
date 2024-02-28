package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class ScoreNotMatchedException extends DuplicationException {
	public ScoreNotMatchedException() {
		super(ErrorCode.SCORE_NOT_MATCHED.getMessage(), ErrorCode.SCORE_NOT_MATCHED);
	}
}
