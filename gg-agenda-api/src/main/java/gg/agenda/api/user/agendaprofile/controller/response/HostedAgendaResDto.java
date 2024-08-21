package gg.agenda.api.user.agendaprofile.controller.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class HostedAgendaResDto {
	private String agendaKey;
	private String agendaTitle;
	private LocalDateTime agendaDeadLine;
	private LocalDateTime agendaStartTime;
	private LocalDateTime agendaEndTime;
	private int agendaCurrentTeam;
	private int agendaMaxTeam;
	private int agendaMinTeam;
	private int agendaMinPeople;
	private int agendaMaxPeople;
	private String agendaLocation;
	private Boolean isRanking;
	private Boolean isOfficial;

	@Builder
	public HostedAgendaResDto(String agendaKey, String agendaTitle, LocalDateTime agendaDeadLine,
		LocalDateTime agendaStartTime, LocalDateTime agendaEndTime, int agendaCurrentTeam, int agendaMaxTeam,
		int agendaMinTeam, int agendaMinPeople, int agendaMaxPeople, String agendaLocation, Boolean isRanking,
		Boolean isOfficial) {
		this.agendaKey = agendaKey;
		this.agendaTitle = agendaTitle;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaCurrentTeam = agendaCurrentTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaLocation = agendaLocation;
		this.isRanking = isRanking;
		this.isOfficial = isOfficial;
	}

	@Mapper
	public interface MapStruct {
		HostedAgendaResDto.MapStruct INSTANCE = Mappers.getMapper(HostedAgendaResDto.MapStruct.class);

		@Mapping(target = "agendaKey", source = "agendaKey")
		@Mapping(target = "agendaTitle", source = "title")
		@Mapping(target = "agendaDeadLine", source = "deadline")
		@Mapping(target = "agendaStartTime", source = "startTime")
		@Mapping(target = "agendaEndTime", source = "endTime")
		@Mapping(target = "agendaCurrentTeam", source = "currentTeam")
		@Mapping(target = "agendaMaxTeam", source = "maxTeam")
		@Mapping(target = "agendaMinTeam", source = "minTeam")
		@Mapping(target = "agendaMinPeople", source = "minPeople")
		@Mapping(target = "agendaMaxPeople", source = "maxPeople")
		@Mapping(target = "agendaLocation", source = "location")
		@Mapping(target = "isRanking", source = "isRanking")
		@Mapping(target = "isOfficial", source = "isOfficial")
		HostedAgendaResDto toDto(Agenda agenda);
	}
}
