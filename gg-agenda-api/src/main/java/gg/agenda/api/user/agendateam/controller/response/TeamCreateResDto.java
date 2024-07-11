package gg.agenda.api.user.agendateam.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamCreateResDto {
	String teamKey;

	public TeamCreateResDto(String teamKey) {
		this.teamKey = teamKey;
	}
}
