package gg.utils.exception.pchange;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class PChangeNotExistException extends NotExistException {
	public PChangeNotExistException() {
		super(ErrorCode.PCHANGE_NOT_FOUND.getMessage(), ErrorCode.PCHANGE_NOT_FOUND);
	}
}
