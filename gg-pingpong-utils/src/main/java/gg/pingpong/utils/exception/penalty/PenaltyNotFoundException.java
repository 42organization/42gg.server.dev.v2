package gg.pingpong.utils.exception.penalty;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class PenaltyNotFoundException extends NotExistException {
	public PenaltyNotFoundException() {
		super("해당 패널티가 없습니다.", ErrorCode.PENALTY_NOT_FOUND);
	}
}
