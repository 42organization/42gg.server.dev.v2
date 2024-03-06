package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;

public class NotHostException extends ForbiddenException {
	public NotHostException() {
		super(ErrorCode.NOT_HOST.getMessage(), ErrorCode.NOT_HOST);
	}
}
