package gg.agenda.api.user.agendaprofile.controller.response;

import java.time.LocalDateTime;
import java.util.UUID;

import gg.data.agenda.AgendaTeamProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CurrentAttendAgendaListResDto {
	private String agendaId;
	private String agendaTitle;
	private String agendaLocation;
	private UUID teamKey;
	private Boolean isOfficial;
	private String teamName;
	private LocalDateTime agendaStartTime;
	private String teamStatus;

	public CurrentAttendAgendaListResDto(AgendaTeamProfile agendaTeamProfile) {
		this.agendaId = agendaTeamProfile.getAgenda().getId().toString();
		this.agendaTitle = agendaTeamProfile.getAgenda().getTitle();
		this.agendaLocation = agendaTeamProfile.getAgenda().getLocation().toString();
		this.teamKey = agendaTeamProfile.getAgendaTeam().getTeamKey();
		this.isOfficial = agendaTeamProfile.getAgenda().getIsOfficial();
		this.teamName = agendaTeamProfile.getAgendaTeam().getName();
		this.agendaStartTime = agendaTeamProfile.getAgenda().getStartTime();
		this.teamStatus = agendaTeamProfile.getAgendaTeam().getStatus().toString();
	}
}
