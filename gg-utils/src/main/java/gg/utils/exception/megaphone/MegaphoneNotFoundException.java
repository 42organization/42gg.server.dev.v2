package gg.utils.exception.megaphone;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class MegaphoneNotFoundException extends NotExistException {
	public MegaphoneNotFoundException() {
		super("확성기를 찾을 수 없습니다.", ErrorCode.MEGAPHONE_TIME);
	}
}
