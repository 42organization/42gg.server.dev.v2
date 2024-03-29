package gg.utils.exception.tournament;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class TournamentNotFoundException extends NotExistException {
	public TournamentNotFoundException() {
		super(ErrorCode.TOURNAMENT_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_NOT_FOUND);
	}

	public TournamentNotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
