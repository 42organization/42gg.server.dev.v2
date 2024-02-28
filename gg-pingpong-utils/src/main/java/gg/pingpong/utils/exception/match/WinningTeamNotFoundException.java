package gg.pingpong.utils.exception.match;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class WinningTeamNotFoundException extends NotExistException {
	public WinningTeamNotFoundException() {
		super(ErrorCode.WINNING_TEAM_NOT_FOUND.getMessage(), ErrorCode.WINNING_TEAM_NOT_FOUND);
	}
}
