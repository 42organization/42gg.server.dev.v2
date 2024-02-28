package gg.utils.exception.season;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class SeasonForbiddenException extends ForbiddenException {
	public SeasonForbiddenException() {
		super(ErrorCode.SEASON_FORBIDDEN.getMessage(), ErrorCode.SEASON_FORBIDDEN);
	}
}
