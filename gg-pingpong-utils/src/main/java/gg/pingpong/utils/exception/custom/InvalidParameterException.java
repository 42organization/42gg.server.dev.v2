package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class InvalidParameterException extends CustomRuntimeException {
	public InvalidParameterException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
