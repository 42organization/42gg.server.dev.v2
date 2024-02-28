package gg.utils.exception.penalty;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ExpiredException;

public class PenaltyExpiredException extends ExpiredException {
	public PenaltyExpiredException() {
		super("이미 만료된 패널티입니다.", ErrorCode.PENALTY_EXPIRED);
	}
}
