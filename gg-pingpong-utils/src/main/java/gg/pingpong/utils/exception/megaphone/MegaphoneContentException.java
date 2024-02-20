package gg.pingpong.utils.exception.megaphone;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class MegaphoneContentException extends BusinessException {
	public MegaphoneContentException() {
		super("확성기 내용이 없습니다.", ErrorCode.MEGAPHONE_CONTENT);
	}
}
