package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class ParseException extends CustomRuntimeException {
	public ParseException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
