package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class EnrolledSlotException extends DuplicationException {
	public EnrolledSlotException() {
		super("이미 등록된 슬롯입니다.", ErrorCode.SLOT_ENROLLED);
	}
}
