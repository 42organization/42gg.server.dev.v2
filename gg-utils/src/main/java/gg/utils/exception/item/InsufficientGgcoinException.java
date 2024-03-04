package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class InsufficientGgcoinException extends BusinessException {
	public InsufficientGgcoinException() {
		super("GGcoin이 부족합니다.", ErrorCode.INSUFFICIENT_GGCOIN);
	}
}
