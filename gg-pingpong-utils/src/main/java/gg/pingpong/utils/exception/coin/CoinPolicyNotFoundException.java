package gg.pingpong.utils.exception.coin;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;

public class CoinPolicyNotFoundException extends CustomRuntimeException {
	public CoinPolicyNotFoundException() {
		super(ErrorCode.CoinPolicy_NOT_FOUND.getMessage(), ErrorCode.CoinPolicy_NOT_FOUND);
	}
}
