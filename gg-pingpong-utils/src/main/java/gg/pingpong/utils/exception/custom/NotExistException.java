package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class NotExistException extends CustomRuntimeException {
	private ErrorCode errorCode;

	public NotExistException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
