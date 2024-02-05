package com.gg.server.domain.tournament.dto;

import com.gg.server.data.game.TournamentGame;
import com.gg.server.data.game.type.TournamentRound;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.GameTeamUser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentGameResDto {

	private Long tournamentGameId;
	private Long nextTournamentGameId;
	private TournamentRound tournamentRound;
	private GameResultResDto game;

	public TournamentGameResDto(TournamentGame tournamentGame, GameTeamUser game, TournamentRound tournamentRound,
		TournamentGame nextTournamentGame) {
		this.tournamentGameId = tournamentGame.getId();
		this.game = game == null ? null : new GameResultResDto(game);
		this.tournamentRound = tournamentRound;
		this.nextTournamentGameId = nextTournamentGame == null ? null : nextTournamentGame.getId();
	}

	@Override
	public String toString() {
		return "TournamentGameResDto{"
			+ "tournamentGameId=" + tournamentGameId
			+ ", NextTournamentGameId=" + nextTournamentGameId
			+ ", tournamentRound='" + tournamentRound + '\''
			+ ", gameId=" + game.getGameId()
			+ '}';
	}
}
