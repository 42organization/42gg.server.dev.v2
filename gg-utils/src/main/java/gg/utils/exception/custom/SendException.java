package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class SendException extends CustomRuntimeException {
	public SendException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
