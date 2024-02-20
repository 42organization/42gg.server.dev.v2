package gg.pingpong.utils.exception.tournament;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class TournamentGameNotFoundException extends NotExistException {
	public TournamentGameNotFoundException() {
		super(ErrorCode.TOURNAMENT_GAME_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_GAME_NOT_FOUND);
	}
}
