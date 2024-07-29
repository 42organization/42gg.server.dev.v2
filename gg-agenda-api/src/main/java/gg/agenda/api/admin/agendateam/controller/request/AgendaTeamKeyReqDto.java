package gg.agenda.api.admin.agendateam.controller.request;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamKeyReqDto {

	@NotNull
	private UUID teamKey;

	@Builder
	public AgendaTeamKeyReqDto(UUID teamKey) {
		this.teamKey = teamKey;
	}
}
