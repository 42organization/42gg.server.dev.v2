package gg.utils.exception.receipt;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class ReceiptNotOwnerException extends ForbiddenException {
	public ReceiptNotOwnerException() {
		super("아이템의 소유자가 아닙니다.", ErrorCode.RECEIPT_NOT_OWNER);
	}
}
