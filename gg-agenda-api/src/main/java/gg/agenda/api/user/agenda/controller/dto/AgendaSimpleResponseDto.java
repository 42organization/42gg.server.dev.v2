package gg.agenda.api.user.agenda.controller.dto;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaSimpleResponseDto {
	private String agendaTitle;

	private String agendaDeadLine;

	private String agendaStartTime;

	private String agendaEndTime;

	private int agendaCurrentTeam;

	private int agendaMaxTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private String agendaLocation;

	private UUID agendaKey;

	private boolean isOfficial;

	@Builder
	public AgendaSimpleResponseDto(String agendaTitle, String agendaDeadLine, String agendaStartTime,
		String agendaEndTime, int agendaCurrentTeam, int agendaMaxTeam, int agendaMinPeople, int agendaMaxPeople,
		String agendaLocation, UUID agendaKey, boolean isOfficial) {
		this.agendaTitle = agendaTitle;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaCurrentTeam = agendaCurrentTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaLocation = agendaLocation;
		this.agendaKey = agendaKey;
		this.isOfficial = isOfficial;
	}

	@Mapper
	public interface MapStruct {
		AgendaSimpleResponseDto.MapStruct INSTANCE = Mappers.getMapper(AgendaSimpleResponseDto.MapStruct.class);

		@Mapping(target = "agendaTitle", source = "title")
		@Mapping(target = "agendaDeadLine", source = "deadline")
		@Mapping(target = "agendaStartTime", source = "startTime")
		@Mapping(target = "agendaEndTime", source = "endTime")
		@Mapping(target = "agendaCurrentTeam", source = "currentTeam")
		@Mapping(target = "agendaMaxTeam", source = "maxTeam")
		@Mapping(target = "agendaMinPeople", source = "minTeam")
		@Mapping(target = "agendaMaxPeople", source = "maxTeam")
		@Mapping(target = "agendaLocation", source = "location")
		@Mapping(target = "agendaKey", source = "agendaKey")
		@Mapping(target = "isOfficial", source = "official")
		AgendaSimpleResponseDto toDto(Agenda agenda);
	}
}
