package gg.agenda.api.user.agendateam.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.agenda.AgendaProfile;
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
	private Coalition[] coalition;

	public ConfirmTeamResDto(AgendaTeam agendaTeam, List<AgendaProfile> agendaProfiles) {
		this.teamName = agendaTeam.getName();
		this.teamLeaderIntraId = agendaTeam.getLeaderIntraId();
		this.teamMateCount = agendaTeam.getMateCount();
		this.teamAward = agendaTeam.getAward();
		this.awardPriority = agendaTeam.getAwardPriority();
		this.coalition = agendaProfiles.stream()
			.map(AgendaProfile::getCoalition)
			.collect(Collectors.toList()).toArray(new Coalition[0]);
	}
}
