package gg.agenda.api.admin.agendateam.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamDetailResDto {

	private String teamName;

	private String teamContent;

	private String teamLeaderIntraId;

	private String teamStatus;

	private String teamAward;

	private int teamAwardPriority;

	private boolean teamIsPrivate;

	private List<AgendaTeamMateResDto> teamMates;

	@Builder
	public AgendaTeamDetailResDto(String teamName, String teamLeaderIntraId, String teamStatus, String teamAward,
		int teamAwardPriority, boolean teamIsPrivate, List<AgendaTeamMateResDto> teamMates, String teamContent) {
		this.teamName = teamName;
		this.teamContent = teamContent;
		this.teamLeaderIntraId = teamLeaderIntraId;
		this.teamStatus = teamStatus;
		this.teamAward = teamAward;
		this.teamAwardPriority = teamAwardPriority;
		this.teamIsPrivate = teamIsPrivate;
		this.teamMates = teamMates;
	}

	@Mapper
	public interface MapStruct {

		AgendaTeamDetailResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaTeamDetailResDto.MapStruct.class);

		@Mapping(target = "teamName", source = "team.name")
		@Mapping(target = "teamContent", source = "team.content")
		@Mapping(target = "teamLeaderIntraId", source = "team.leaderIntraId")
		@Mapping(target = "teamStatus", source = "team.status")
		@Mapping(target = "teamAward", source = "team.award")
		@Mapping(target = "teamAwardPriority", source = "team.awardPriority")
		@Mapping(target = "teamIsPrivate", source = "team.isPrivate")
		@Mapping(target = "teamMates", source = "teamMates", qualifiedByName = "toAgendaProfileResDtoList")
		AgendaTeamDetailResDto toDto(AgendaTeam team, List<AgendaProfile> teamMates);

		@Named("toAgendaProfileResDtoList")
		default List<AgendaTeamMateResDto> toAgendaProfileResDtoList(List<AgendaProfile> teamMates) {
			return teamMates.stream()
				.map(AgendaTeamMateResDto.MapStruct.INSTANCE::toDto)
				.collect(Collectors.toList());
		}
	}
}
