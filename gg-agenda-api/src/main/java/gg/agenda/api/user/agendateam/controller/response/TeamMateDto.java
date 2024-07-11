package gg.agenda.api.user.agendateam.controller.response;

import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.Coalition;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TeamMateDto {
	private String intraId;
	private Coalition coalition;

	public TeamMateDto(AgendaTeamProfile agendaTeamProfile) {
		this.intraId = agendaTeamProfile.getProfile().getIntraId();
		this.coalition = agendaTeamProfile.getProfile().getCoalition();
	}
}
