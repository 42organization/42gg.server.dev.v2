package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DBConsistencyException extends CustomRuntimeException {
	public DBConsistencyException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
