package com.gg.server.domain.receipt.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class ReceiptNotFoundException extends NotExistException {
	public ReceiptNotFoundException() {
		super("해당 거래내역이 없습니다.", ErrorCode.RECEIPT_NOT_FOUND);
	}
}
