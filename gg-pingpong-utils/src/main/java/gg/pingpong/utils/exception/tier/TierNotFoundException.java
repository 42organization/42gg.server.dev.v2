package gg.pingpong.utils.exception.tier;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class TierNotFoundException extends NotExistException {
	public TierNotFoundException() {
		super("해당 티어가 존재하지 않습니다.", ErrorCode.TIER_NOT_FOUND);
	}

}
