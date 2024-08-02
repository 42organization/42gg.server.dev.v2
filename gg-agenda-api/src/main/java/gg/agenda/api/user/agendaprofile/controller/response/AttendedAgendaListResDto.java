package gg.agenda.api.user.agendaprofile.controller.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import gg.agenda.api.user.agendateam.controller.response.TeamMateDto;
import gg.data.agenda.AgendaTeamProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AttendedAgendaListResDto {
	private String agendaId;
	private String agendaTitle;
	private LocalDateTime agendaStartTime;
	private LocalDateTime agendaEndTime;
	private int agendaCurrentTeam;
	private String agendaLocation;
	private UUID teamKey;
	private boolean isOfficial;
	private int agendaMaxPeople;
	private String teamName;
	private List<TeamMateDto> teamMates;

	public AttendedAgendaListResDto(AgendaTeamProfile agendaTeamProfile,
		List<AgendaTeamProfile> agendaTeamProfileList) {
		this.agendaId = agendaTeamProfile.getAgenda().getId().toString();
		this.agendaTitle = agendaTeamProfile.getAgenda().getTitle();
		this.agendaStartTime = agendaTeamProfile.getAgenda().getStartTime();
		this.agendaEndTime = agendaTeamProfile.getAgenda().getEndTime();
		this.agendaCurrentTeam = agendaTeamProfile.getAgenda().getCurrentTeam();
		this.agendaLocation = agendaTeamProfile.getAgenda().getLocation().toString();
		this.teamKey = agendaTeamProfile.getAgendaTeam().getTeamKey();
		this.isOfficial = agendaTeamProfile.getAgenda().getIsOfficial();
		this.agendaMaxPeople = agendaTeamProfile.getAgenda().getMaxPeople();
		this.teamName = agendaTeamProfile.getAgendaTeam().getName();
		this.teamMates = agendaTeamProfileList.stream()
			.map(TeamMateDto::new)
			.collect(Collectors.toList());
	}
}
