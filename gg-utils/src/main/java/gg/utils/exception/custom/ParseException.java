package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class ParseException extends CustomRuntimeException {
	public ParseException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
