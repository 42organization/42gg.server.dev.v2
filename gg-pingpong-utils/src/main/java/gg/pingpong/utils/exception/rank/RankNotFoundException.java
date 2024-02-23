package gg.pingpong.utils.exception.rank;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class RankNotFoundException extends NotExistException {
	public RankNotFoundException() {
		super("랭크 테이블에 없는 유저입니다.", ErrorCode.RANK_NOT_FOUND);
	}
}
