package gg.agenda.api.user.agendateam.controller.response;

import java.util.List;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.Coalition;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ConfirmTeamResDto {
	private String teamName;
	private String teamLeaderIntraId;
	private int teamMateCount;
	private String teamAward;
	private int awardPriority;
	private List<Coalition> coalitions;

	public ConfirmTeamResDto(AgendaTeam agendaTeam, List<Coalition> coalitions) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamMateCount = agendaTeam.getMateCount();
		this.teamAward = agendaTeam.getAward();
		this.awardPriority = agendaTeam.getAwardPriority();
		this.coalitions = coalitions;
	}
}
