package com.gg.server.domain.pchange.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class PChangeNotExistException extends NotExistException {
	public PChangeNotExistException() {
		super("이전 exp 히스토리가 존재하지 않습니다.", ErrorCode.PCHANGE_NOT_FOUND);
	}
}
