package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class DuplicationException extends CustomRuntimeException {
	public DuplicationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
