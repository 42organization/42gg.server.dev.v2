package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class PenaltyUserSlotException extends BusinessException {
	public PenaltyUserSlotException() {
		super("패널티 받은 유저는 게임을 등록할 수 없습니다.", ErrorCode.PENALTY_USER_ENROLLED);
	}
}
