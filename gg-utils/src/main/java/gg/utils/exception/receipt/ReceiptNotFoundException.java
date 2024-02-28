package gg.utils.exception.receipt;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class ReceiptNotFoundException extends NotExistException {
	public ReceiptNotFoundException() {
		super("해당 거래내역이 없습니다.", ErrorCode.RECEIPT_NOT_FOUND);
	}
}
