package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class SendException extends CustomRuntimeException {
	public SendException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
