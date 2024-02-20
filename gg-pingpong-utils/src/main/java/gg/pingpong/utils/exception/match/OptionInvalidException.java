package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.InvalidParameterException;

public class OptionInvalidException extends InvalidParameterException {
	public OptionInvalidException() {
		super("존재하지 않은 mode 입니다", ErrorCode.MODE_INVALID);
	}
}
