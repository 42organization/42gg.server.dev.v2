package gg.pingpong.utils.exception.season;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ForbiddenException;

public class SeasonForbiddenException extends ForbiddenException {
	public SeasonForbiddenException() {
		super(ErrorCode.SEASON_FORBIDDEN.getMessage(), ErrorCode.SEASON_FORBIDDEN);
	}
}
