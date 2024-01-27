package com.gg.server.domain.penalty.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class PenaltyNotFoundException extends NotExistException {
	public PenaltyNotFoundException() {
		super("해당 패널티가 없습니다.", ErrorCode.PENALTY_NOT_FOUND);
	}
}
