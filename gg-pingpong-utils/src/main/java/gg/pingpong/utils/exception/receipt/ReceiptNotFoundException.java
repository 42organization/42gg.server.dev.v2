package gg.pingpong.utils.exception.receipt;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class ReceiptNotFoundException extends NotExistException {
	public ReceiptNotFoundException() {
		super("해당 거래내역이 없습니다.", ErrorCode.RECEIPT_NOT_FOUND);
	}
}
