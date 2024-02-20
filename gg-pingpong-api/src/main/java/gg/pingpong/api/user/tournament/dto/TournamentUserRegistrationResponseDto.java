package gg.pingpong.api.user.tournament.dto;

import com.gg.server.data.game.type.TournamentUserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserRegistrationResponseDto {
	private TournamentUserStatus status;
}
