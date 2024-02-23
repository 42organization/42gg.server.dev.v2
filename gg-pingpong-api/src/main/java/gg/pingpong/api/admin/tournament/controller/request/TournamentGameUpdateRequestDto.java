package gg.pingpong.api.admin.tournament.controller.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import gg.pingpong.api.user.game.dto.TeamReqDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TournamentGameUpdateRequestDto {

	@NotNull
	private Long tournamentGameId;

	private Long nextTournamentGameId;
	@NotNull
	@Valid
	private TeamReqDto team1;
	@NotNull
	@Valid
	private TeamReqDto team2;

	@Override
	public String toString() {
		return "TournamentGameUpdateReqDto{"
			+ "tournamentGameId=" + tournamentGameId
			+ ", nextTournamentGameId=" + nextTournamentGameId
			+ ", team1=" + team1 + ", team2="
			+ team2 + '}';
	}
}
