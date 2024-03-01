package gg.utils.exception.season;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class SeasonNotFoundException extends NotExistException {
	public SeasonNotFoundException() {
		super("시즌이 없습니다.", ErrorCode.SEASON_NOT_FOUND);
	}

	public SeasonNotFoundException(String message) {
		super(message, ErrorCode.SEASON_NOT_FOUND);
	}
}
