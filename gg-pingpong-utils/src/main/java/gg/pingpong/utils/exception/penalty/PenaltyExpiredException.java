package gg.pingpong.utils.exception.penalty;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ExpiredException;

public class PenaltyExpiredException extends ExpiredException {
	public PenaltyExpiredException() {
		super("이미 만료된 패널티입니다.", ErrorCode.PENALTY_EXPIRED);
	}
}
