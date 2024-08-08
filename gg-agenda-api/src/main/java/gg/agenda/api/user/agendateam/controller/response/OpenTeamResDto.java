package gg.agenda.api.user.agendateam.controller.response;

import java.util.List;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.Coalition;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OpenTeamResDto {
	private String teamName;
	private String teamLeaderIntraId;
	private int teamMateCount;
	private String teamKey;
	private List<Coalition> coalitions;

	public OpenTeamResDto(AgendaTeam agendaTeam, List<Coalition> coalitions) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamMateCount = agendaTeam.getMateCount();
		this.teamKey = agendaTeam.getTeamKey().toString();
		this.coalitions = coalitions;
	}
}
