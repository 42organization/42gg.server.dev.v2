package gg.agenda.api.admin.agendateam.controller.response;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamResDto {

	private String teamName;

	private String teamContent;

	private String teamStatus;

	private boolean teamIsPrivate;

	private String teamLeaderIntraId;

	private int teamMateCount;

	private UUID teamKey;

	private String teamAward;

	private Location teamLocation;

	private Integer teamAwardPriority;

	@Builder
	public AgendaTeamResDto(String teamName, String teamStatus, boolean teamIsPrivate, String teamLeaderIntraId,
		int teamMateCount, UUID teamKey, String teamAward, Integer teamAwardPriority, String teamContent,
		Location teamLocation) {
		this.teamName = teamName;
		this.teamContent = teamContent;
		this.teamStatus = teamStatus;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLeaderIntraId = teamLeaderIntraId;
		this.teamMateCount = teamMateCount;
		this.teamKey = teamKey;
		this.teamAward = teamAward;
		this.teamAwardPriority = teamAwardPriority;
		this.teamLocation = teamLocation;
	}

	@Mapper
	public interface MapStruct {

		AgendaTeamResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaTeamResDto.MapStruct.class);

		@Mapping(target = "teamName", source = "name")
		@Mapping(target = "teamContent", source = "content")
		@Mapping(target = "teamStatus", source = "status")
		@Mapping(target = "teamIsPrivate", source = "isPrivate")
		@Mapping(target = "teamLeaderIntraId", source = "leaderIntraId")
		@Mapping(target = "teamMateCount", source = "mateCount")
		@Mapping(target = "teamKey", source = "teamKey")
		@Mapping(target = "teamLocation", source = "location")
		@Mapping(target = "teamAward", source = "award", defaultValue = "AgendaTeam.DEFAULT_AWARD")
		@Mapping(target = "teamAwardPriority", source = "awardPriority", defaultValue = "0")
		AgendaTeamResDto toDto(AgendaTeam agendaTeam);
	}
}
