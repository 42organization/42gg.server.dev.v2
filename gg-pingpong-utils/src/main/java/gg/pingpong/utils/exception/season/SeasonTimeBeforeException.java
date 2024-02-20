package gg.pingpong.utils.exception.season;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ForbiddenException;

public class SeasonTimeBeforeException extends ForbiddenException {
	public SeasonTimeBeforeException() {
		super(ErrorCode.SEASON_TIME_BEFORE.getMessage(), ErrorCode.SEASON_TIME_BEFORE);
	}
}
