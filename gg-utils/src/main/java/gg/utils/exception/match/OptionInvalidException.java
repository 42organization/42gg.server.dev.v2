package gg.utils.exception.match;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class OptionInvalidException extends InvalidParameterException {
	public OptionInvalidException() {
		super("존재하지 않은 mode 입니다", ErrorCode.MODE_INVALID);
	}
}
