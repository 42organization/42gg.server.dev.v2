package gg.agenda.api.admin.agendateam.controller.request;

import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamUpdateDto {

	private UUID teamKey;
	private String teamName;
	private String teamContent;
	private AgendaTeamStatus teamStatus;
	private Boolean teamIsPrivate;
	private Location teamLocation;
	private String teamAward;
	private Integer teamAwardPriority;
	private List<AgendaTeamMateReqDto> teamMates;

	@Builder
	public AgendaTeamUpdateDto(UUID teamKey, String teamName, String teamContent, AgendaTeamStatus teamStatus,
		Location teamLocation, String teamAward, Integer teamAwardPriority, Boolean teamIsPrivate,
		List<AgendaTeamMateReqDto> teamMates) {
		this.teamKey = teamKey;
		this.teamName = teamName;
		this.teamContent = teamContent;
		this.teamStatus = teamStatus;
		this.teamAward = teamAward;
		this.teamAwardPriority = teamAwardPriority;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLocation = teamLocation;
		this.teamMates = teamMates;
	}
}
