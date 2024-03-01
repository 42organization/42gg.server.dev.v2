package gg.utils.exception.match;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class LosingTeamNotFoundException extends NotExistException {
	public LosingTeamNotFoundException() {
		super(ErrorCode.LOSING_TEAM_NOT_FOUND.getMessage(), ErrorCode.LOSING_TEAM_NOT_FOUND);
	}
}
