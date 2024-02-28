package gg.utils.exception.rank;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class RankUpdateException extends InvalidParameterException {
	public RankUpdateException() {
		super("Ppp를 수정 할 수 없습니다", ErrorCode.RANK_UPDATE_FAIL);
	}
}
