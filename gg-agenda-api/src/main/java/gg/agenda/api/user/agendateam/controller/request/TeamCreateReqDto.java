package gg.agenda.api.user.agendateam.controller.request;

import static gg.data.agenda.type.AgendaTeamStatus.*;

import java.util.UUID;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamCreateReqDto {
	private String teamName;
	private boolean teamIsPrivate;
	private String teamLocation;
	private String teamContent;

	public static AgendaTeam toEntity(TeamCreateReqDto teamCreateReqDto, Agenda agenda, String intraId) {
		return AgendaTeam.builder()
			.agenda(agenda)
			.teamKey(UUID.randomUUID())
			.name(teamCreateReqDto.getTeamName())
			.content(teamCreateReqDto.getTeamContent())
			.leaderIntraId(intraId)
			.status(String.valueOf(OPEN))
			.location(teamCreateReqDto.getTeamLocation())
			.mateCount(1)
			.award("award")
			.awardPriority(1)
			.isPrivate(teamCreateReqDto.isTeamIsPrivate())
			.build();
	}
}
