package gg.agenda.api.user.agendateam.controller.request;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamKeyReqDto {
	@NotNull
	private UUID teamKey;

	public TeamKeyReqDto(UUID teamKey) {
		this.teamKey = teamKey;
	}
}
