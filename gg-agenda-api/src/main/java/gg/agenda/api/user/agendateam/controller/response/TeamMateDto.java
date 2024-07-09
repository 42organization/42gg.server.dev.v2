package gg.agenda.api.user.agendateam.controller.response;

import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.Coalition;
import lombok.Setter;

@Setter
public class TeamMateDto {
	private String intraId;
	private Coalition coalition;

	TeamMateDto(AgendaTeamProfile agendaTeamProfile) {
		this.intraId = agendaTeamProfile.getProfile().getIntraId();
		this.coalition = agendaTeamProfile.getProfile().getCoalition();
	}
}
