package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class InsufficientGgcoinException extends BusinessException {
	public InsufficientGgcoinException() {
		super("GGcoin이 부족합니다.", ErrorCode.INSUFFICIENT_GGCOIN);
	}
}
