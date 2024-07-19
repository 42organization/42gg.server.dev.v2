package gg.agenda.api.user.agendateam.controller.response;

import gg.data.agenda.AgendaTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OpenTeamResDto {
	private String teamName;
	private String teamLeaderIntraId;
	private int teamMateCount;
	private String teamKey;

	public OpenTeamResDto(AgendaTeam agendaTeam) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamMateCount = agendaTeam.getMateCount();
		this.teamKey = agendaTeam.getTeamKey().toString();
	}
}
