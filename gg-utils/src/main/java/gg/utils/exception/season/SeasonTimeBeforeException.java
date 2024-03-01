package gg.utils.exception.season;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class SeasonTimeBeforeException extends ForbiddenException {
	public SeasonTimeBeforeException() {
		super(ErrorCode.SEASON_TIME_BEFORE.getMessage(), ErrorCode.SEASON_TIME_BEFORE);
	}
}
