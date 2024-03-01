package gg.utils.exception.tournament;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class TournamentUpdateException extends ForbiddenException {
	public TournamentUpdateException() {
		super(ErrorCode.TOURNAMENT_CAN_NOT_UPDATE.getMessage(), ErrorCode.TOURNAMENT_CAN_NOT_UPDATE);
	}

	public TournamentUpdateException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
