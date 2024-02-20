package gg.pingpong.utils.exception.season;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class SeasonNotFoundException extends NotExistException {
	public SeasonNotFoundException() {
		super("시즌이 없습니다.", ErrorCode.SEASON_NOT_FOUND);
	}

	public SeasonNotFoundException(String message) {
		super(message, ErrorCode.SEASON_NOT_FOUND);
	}
}
