package gg.agenda.api.user.agendateam.controller.request;

import static gg.data.agenda.type.AgendaTeamStatus.*;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TeamCreateReqDto {
	@NotBlank
	@Size(max = 30)
	private String teamName;

	@NotNull
	private Boolean teamIsPrivate;

	@NotBlank
	@Size(max = 10)
	private String teamLocation;

	@NotBlank
	@Size(max = 500)
	private String teamContent;

	@Builder
	public TeamCreateReqDto(String teamName, Boolean teamIsPrivate, String teamLocation, String teamContent) {
		this.teamName = teamName;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLocation = teamLocation;
		this.teamContent = teamContent;
	}

	public static AgendaTeam toEntity(TeamCreateReqDto teamCreateReqDto, Agenda agenda, String intraId) {
		return AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name(teamCreateReqDto.getTeamName())
			.content(teamCreateReqDto.getTeamContent())
			.leaderIntraId(intraId)
			.status(OPEN)
			.location(Location.valueOf(teamCreateReqDto.getTeamLocation()))
			.mateCount(1)
			.awardPriority(1)
			.isPrivate(teamCreateReqDto.getTeamIsPrivate())
			.build();
	}
}
