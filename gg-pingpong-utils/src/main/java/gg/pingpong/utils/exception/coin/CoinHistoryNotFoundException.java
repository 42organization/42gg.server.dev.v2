package gg.pingpong.utils.exception.coin;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;

public class CoinHistoryNotFoundException extends CustomRuntimeException {
	public CoinHistoryNotFoundException() {
		super(ErrorCode.COIN_HISTORY_NOT_FOUND.getMessage(), ErrorCode.COIN_HISTORY_NOT_FOUND);
	}
}
