package gg.pingpong.api.user.tournament.controller.response;

import gg.pingpong.data.game.type.TournamentUserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserRegistrationResponseDto {
	private TournamentUserStatus status;
}
