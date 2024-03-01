package gg.utils.exception.receipt;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class ItemStatusException extends BusinessException {
	public ItemStatusException() {
		super("아이템 상태 오류입니다.", ErrorCode.RECEIPT_STATUS_NOT_MATCHED);
	}
}
