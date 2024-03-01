package gg.utils.exception.coin;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class CoinHistoryNotFoundException extends CustomRuntimeException {
	public CoinHistoryNotFoundException() {
		super(ErrorCode.COIN_HISTORY_NOT_FOUND.getMessage(), ErrorCode.COIN_HISTORY_NOT_FOUND);
	}
}
