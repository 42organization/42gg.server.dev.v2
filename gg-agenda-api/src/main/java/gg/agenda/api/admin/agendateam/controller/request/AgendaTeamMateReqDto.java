package gg.agenda.api.admin.agendateam.controller.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamMateReqDto {

	@NotNull
	private String intraId;

	@Builder
	public AgendaTeamMateReqDto(String intraId) {
		this.intraId = intraId;
	}
}
