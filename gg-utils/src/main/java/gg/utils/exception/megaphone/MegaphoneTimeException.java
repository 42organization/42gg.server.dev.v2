package gg.utils.exception.megaphone;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class MegaphoneTimeException extends BusinessException {
	public MegaphoneTimeException() {
		super("확성기 사용이 불가능한 시간입니다.", ErrorCode.MEGAPHONE_TIME);
	}
}
