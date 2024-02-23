package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class FileException extends CustomRuntimeException {

	public FileException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
