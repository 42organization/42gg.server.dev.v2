package gg.agenda.api.user.agendateam.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamKeyResDto {
	String teamKey;

	public TeamKeyResDto(String teamKey) {
		this.teamKey = teamKey;
	}
}
