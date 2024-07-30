package gg.agenda.api.admin.agendateam.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamMateReqDto {

	private String intraId;

	@Builder
	public AgendaTeamMateReqDto(String intraId) {
		this.intraId = intraId;
	}
}
