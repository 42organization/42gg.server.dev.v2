package gg.agenda.api.user.agendateam.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDetailsResDto {
	private String teamName;
	private String teamLeaderIntraId;
	private AgendaTeamStatus teamStatus;
	private Location teamLocation;
	private String teamContent;
	private List<TeamMateDto> teamMates;

	public TeamDetailsResDto(AgendaTeam agendaTeam, List<AgendaTeamProfile> agendaTeamProfileList) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamStatus = agendaTeam.getStatus();
		this.teamLocation = agendaTeam.getLocation();
		this.teamContent = agendaTeam.getContent();
		this.teamMates = agendaTeamProfileList.stream()
			.map(TeamMateDto::new)
			.collect(Collectors.toList());
	}
}
