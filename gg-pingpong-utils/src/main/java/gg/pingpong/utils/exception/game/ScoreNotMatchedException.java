package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class ScoreNotMatchedException extends DuplicationException {
	public ScoreNotMatchedException() {
		super(ErrorCode.SCORE_NOT_MATCHED.getMessage(), ErrorCode.SCORE_NOT_MATCHED);
	}
}
