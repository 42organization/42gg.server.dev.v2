package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class KakaoPurchaseException extends BusinessException {
	public KakaoPurchaseException() {
		super("카카오 게스트는 구매할 수 없습니다.", ErrorCode.GUEST_ROLE_PURCHASE_FORBIDDEN);
	}
}

