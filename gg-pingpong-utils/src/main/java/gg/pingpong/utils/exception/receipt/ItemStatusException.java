package gg.pingpong.utils.exception.receipt;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class ItemStatusException extends BusinessException {
	public ItemStatusException() {
		super("아이템 상태 오류입니다.", ErrorCode.RECEIPT_STATUS_NOT_MATCHED);
	}
}
