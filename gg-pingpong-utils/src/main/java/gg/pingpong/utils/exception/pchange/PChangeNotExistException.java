package gg.pingpong.utils.exception.pchange;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class PChangeNotExistException extends NotExistException {
	public PChangeNotExistException() {
		super(ErrorCode.PCHANGE_NOT_FOUND.getMessage(), ErrorCode.PCHANGE_NOT_FOUND);
	}
}
