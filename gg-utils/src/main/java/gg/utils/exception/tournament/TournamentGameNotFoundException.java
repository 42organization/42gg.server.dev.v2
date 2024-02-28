package gg.utils.exception.tournament;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class TournamentGameNotFoundException extends NotExistException {
	public TournamentGameNotFoundException() {
		super(ErrorCode.TOURNAMENT_GAME_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_GAME_NOT_FOUND);
	}
}
