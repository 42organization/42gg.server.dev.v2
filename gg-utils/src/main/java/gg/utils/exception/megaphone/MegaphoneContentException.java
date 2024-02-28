package gg.utils.exception.megaphone;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class MegaphoneContentException extends BusinessException {
	public MegaphoneContentException() {
		super("확성기 내용이 없습니다.", ErrorCode.MEGAPHONE_CONTENT);
	}
}
