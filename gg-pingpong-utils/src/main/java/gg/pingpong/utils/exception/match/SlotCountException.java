package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class SlotCountException extends BusinessException {
	public SlotCountException() {
		super("슬롯 등록 횟수 3회를 초과했습니다.", ErrorCode.SLOT_COUNT_EXCEEDED);
	}
}
