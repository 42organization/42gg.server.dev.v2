package gg.utils.exception.team;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class TeamNotFoundException extends NotExistException {

	public TeamNotFoundException() {
		super(ErrorCode.TEAM_NOT_FOUND.getMessage(), ErrorCode.TEAM_NOT_FOUND);
	}
}
