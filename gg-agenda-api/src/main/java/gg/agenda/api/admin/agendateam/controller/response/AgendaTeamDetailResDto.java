package gg.agenda.api.admin.agendateam.controller.response;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamDetailResDto {

	private String teamName;

	private String teamLeaderIntraId;

	private String teamStatus;

	private int teamScore;

	private boolean teamIsPrivate;

	private List<AgendaProfileResDto> teamMates;

	@Builder
	public AgendaTeamDetailResDto(String teamName, String teamLeaderIntraId, String teamStatus, int teamScore,
		boolean teamIsPrivate, List<AgendaProfileResDto> teamMates) {
		this.teamName = teamName;
		this.teamLeaderIntraId = teamLeaderIntraId;
		this.teamStatus = teamStatus;
		this.teamScore = teamScore;
		this.teamIsPrivate = teamIsPrivate;
		this.teamMates = teamMates;
	}

	@Mapper
	public interface MapStruct {

		AgendaTeamDetailResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaTeamDetailResDto.MapStruct.class);

		@Mapping(target = "teamName", source = "team.name")
		@Mapping(target = "teamLeaderIntraId", source = "team.leaderIntraId")
		@Mapping(target = "teamStatus", source = "team.status")
		@Mapping(target = "teamScore", source = "team.score")
		@Mapping(target = "teamIsPrivate", source = "team.isPrivate")
		@Mapping(target = "teamMates", source = "teamMates", qualifiedByName = "toAgendaProfileResDtoList")
		AgendaTeamDetailResDto toDto(AgendaTeam team, List<AgendaProfile> teamMates);

		@Named("toAgendaProfileResDtoList")
		default List<AgendaProfileResDto> toAgendaProfileResDtoList(List<AgendaProfile> teamMates) {
			return teamMates.stream()
				.map(AgendaProfileResDto.MapStruct.INSTANCE::toDto)
				.collect(Collectors.toList());
		}
	}
}
