package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ExpiredException;

public class PastSlotException extends ExpiredException {
	public PastSlotException() {
		super("현재 시각 이전 슬롯은 등록할 수 없습니다.", ErrorCode.SLOT_PAST);
	}
}
