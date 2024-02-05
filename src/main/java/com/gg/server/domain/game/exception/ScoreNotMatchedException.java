package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class ScoreNotMatchedException extends DuplicationException {
	public ScoreNotMatchedException() {
		super(ErrorCode.SCORE_NOT_MATCHED.getMessage(), ErrorCode.SCORE_NOT_MATCHED);
	}
}
