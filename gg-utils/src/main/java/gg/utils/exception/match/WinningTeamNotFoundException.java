package gg.utils.exception.match;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class WinningTeamNotFoundException extends NotExistException {
	public WinningTeamNotFoundException() {
		super(ErrorCode.WINNING_TEAM_NOT_FOUND.getMessage(), ErrorCode.WINNING_TEAM_NOT_FOUND);
	}
}
