package gg.pingpong.api.user.tournament.controller.response;

import java.time.LocalDateTime;

import gg.pingpong.data.game.TournamentUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserResponseDto {
	private Long userId;
	private String intraId;
	private Boolean isJoined;
	private LocalDateTime registeredDate;

	public TournamentUserResponseDto(TournamentUser tournamentUser) {
		this.userId = tournamentUser.getUser().getId();
		this.intraId = tournamentUser.getUser().getIntraId();
		this.isJoined = tournamentUser.getIsJoined();
		this.registeredDate = tournamentUser.getRegisterTime();
	}
}
