package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class KakaoGiftException extends BusinessException {
	public KakaoGiftException() {
		super("카카오 게스트는 선물할 수 없습니다.", ErrorCode.GUEST_ROLE_GIFT_FORBIDDEN);
	}
}
