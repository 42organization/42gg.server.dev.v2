package gg.agenda.api.admin.agendateam.controller.response;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.AgendaTeam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamResDto {

	private String teamName;

	private String teamStatus;

	private int teamScore;

	private boolean teamIsPrivate;

	private String teamLeaderIntraId;

	private int teamMateCount;

	private UUID teamKey;

	@Builder
	public AgendaTeamResDto(String teamName, String teamStatus, int teamScore, boolean teamIsPrivate,
		String teamLeaderIntraId, int teamMateCount, UUID teamKey) {
		this.teamName = teamName;
		this.teamStatus = teamStatus;
		this.teamScore = teamScore;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLeaderIntraId = teamLeaderIntraId;
		this.teamMateCount = teamMateCount;
		this.teamKey = teamKey;
	}

	@Mapper
	public interface MapStruct {

		AgendaTeamResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaTeamResDto.MapStruct.class);

		AgendaTeamResDto toDto(AgendaTeam agendaTeam);
	}
}
