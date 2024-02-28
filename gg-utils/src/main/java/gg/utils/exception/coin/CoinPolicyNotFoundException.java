package gg.utils.exception.coin;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class CoinPolicyNotFoundException extends CustomRuntimeException {
	public CoinPolicyNotFoundException() {
		super(ErrorCode.CoinPolicy_NOT_FOUND.getMessage(), ErrorCode.CoinPolicy_NOT_FOUND);
	}
}
