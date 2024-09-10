package gg.agenda.api.user.agendateam.controller.response;

import java.util.List;
import java.util.UUID;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyTeamSimpleResDto {
	private String teamName;
	private String teamLeaderIntraId;
	private int teamMateCount;
	private AgendaTeamStatus teamStatus;
	private UUID teamKey;
	private Location teamLocation;
	private String teamAward;
	private List<Coalition> coalitions;

	public MyTeamSimpleResDto(AgendaTeam agendaTeam, List<Coalition> coalitions) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamMateCount = agendaTeam.getMateCount();
		this.teamStatus = agendaTeam.getStatus();
		this.teamKey = agendaTeam.getTeamKey();
		this.teamLocation = agendaTeam.getLocation();
		this.teamAward = agendaTeam.getAward();
		this.coalitions = coalitions;
	}
}
