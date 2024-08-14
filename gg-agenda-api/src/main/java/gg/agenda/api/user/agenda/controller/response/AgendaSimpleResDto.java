package gg.agenda.api.user.agenda.controller.response;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaSimpleResDto {

	private String agendaTitle;

	private LocalDateTime agendaDeadLine;

	private LocalDateTime agendaStartTime;

	private LocalDateTime agendaEndTime;

	private int agendaCurrentTeam;

	private int agendaMaxTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private Location agendaLocation;

	private UUID agendaKey;

	private Boolean isOfficial;

	private Boolean isRanking;

	private URL agendaPosterUrl;

	@Builder
	public AgendaSimpleResDto(String agendaTitle, LocalDateTime agendaDeadLine, LocalDateTime agendaStartTime,
		LocalDateTime agendaEndTime, int agendaCurrentTeam, int agendaMaxTeam, int agendaMinPeople,
		int agendaMaxPeople, Location agendaLocation, UUID agendaKey, Boolean isOfficial, Boolean isRanking,
		URL agendaPosterUrl) {
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
		this.isRanking = isRanking;
		this.agendaPosterUrl = agendaPosterUrl;
	}

	@Mapper
	public interface MapStruct {
		AgendaSimpleResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaSimpleResDto.MapStruct.class);

		@Mapping(target = "agendaTitle", source = "title")
		@Mapping(target = "agendaDeadLine", source = "deadline")
		@Mapping(target = "agendaStartTime", source = "startTime")
		@Mapping(target = "agendaEndTime", source = "endTime")
		@Mapping(target = "agendaCurrentTeam", source = "currentTeam")
		@Mapping(target = "agendaMaxTeam", source = "maxTeam")
		@Mapping(target = "agendaMinPeople", source = "minPeople")
		@Mapping(target = "agendaMaxPeople", source = "maxPeople")
		@Mapping(target = "agendaLocation", source = "location")
		@Mapping(target = "agendaKey", source = "agendaKey")
		@Mapping(target = "isOfficial", source = "isOfficial")
		@Mapping(target = "isRanking", source = "isRanking")
		@Mapping(target = "agendaPosterUrl", source = "posterUri")
		AgendaSimpleResDto toDto(Agenda agenda);
	}
}
