package gg.utils.exception.match;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class SlotNotFoundException extends NotExistException {
	public SlotNotFoundException() {
		super("유저가 등록한 슬롯이 없습니다.", ErrorCode.SLOT_NOT_FOUND);
	}
}
