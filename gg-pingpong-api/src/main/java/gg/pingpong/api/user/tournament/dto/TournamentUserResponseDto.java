package gg.pingpong.api.user.tournament.dto;

import java.time.LocalDateTime;

import com.gg.server.data.game.TournamentUser;

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
