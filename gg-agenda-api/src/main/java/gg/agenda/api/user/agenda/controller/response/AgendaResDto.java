package gg.agenda.api.user.agenda.controller.response;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaResDto {

	private String agendaTitle;

	private String agendaContents;

	private LocalDateTime agendaDeadLine;

	private LocalDateTime agendaStartTime;

	private LocalDateTime agendaEndTime;

	private int agendaMinTeam;

	private int agendaMaxTeam;

	private int agendaCurrentTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private String agendaPoster;

	private String agendaHost;

	private Location agendaLocation;

	private AgendaStatus agendaStatus;

	private LocalDateTime createdAt;

	private Boolean isOfficial;

	private String announcementTitle;

	@Builder
	public AgendaResDto(String agendaTitle, String agendaContents, LocalDateTime agendaDeadLine,
		LocalDateTime agendaStartTime, LocalDateTime agendaEndTime, int agendaMinTeam, int agendaMaxTeam,
		int agendaCurrentTeam, int agendaMinPeople, int agendaMaxPeople, String agendaPoster, String agendaHost,
		Location agendaLocation, AgendaStatus agendaStatus, LocalDateTime createdAt, String announcementTitle,
		boolean isOfficial) {
		this.agendaTitle = agendaTitle;
		this.agendaContents = agendaContents;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaCurrentTeam = agendaCurrentTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaPoster = agendaPoster;
		this.agendaHost = agendaHost;
		this.agendaLocation = agendaLocation;
		this.agendaStatus = agendaStatus;
		this.createdAt = createdAt;
		this.announcementTitle = announcementTitle;
		this.isOfficial = isOfficial;
	}

	@Mapper
	public interface MapStruct {

		AgendaResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaResDto.MapStruct.class);

		@Mapping(target = "agendaTitle", source = "agenda.title")
		@Mapping(target = "agendaContents", source = "agenda.content")
		@Mapping(target = "agendaDeadLine", source = "agenda.deadline")
		@Mapping(target = "agendaStartTime", source = "agenda.startTime")
		@Mapping(target = "agendaEndTime", source = "agenda.endTime")
		@Mapping(target = "agendaMinTeam", source = "agenda.minTeam")
		@Mapping(target = "agendaMaxTeam", source = "agenda.maxTeam")
		@Mapping(target = "agendaCurrentTeam", source = "agenda.currentTeam")
		@Mapping(target = "agendaMinPeople", source = "agenda.minPeople")
		@Mapping(target = "agendaMaxPeople", source = "agenda.maxPeople")
		@Mapping(target = "agendaPoster", source = "agenda.posterUri")
		@Mapping(target = "agendaHost", source = "agenda.hostIntraId")
		@Mapping(target = "agendaLocation", source = "agenda.location")
		@Mapping(target = "agendaStatus", source = "agenda.status")
		@Mapping(target = "createdAt", source = "agenda.createdAt")
		@Mapping(target = "isOfficial", source = "agenda.isOfficial")
		@Mapping(target = "announcementTitle", source = "announcementTitle")
		AgendaResDto toDto(Agenda agenda, String announcementTitle);
	}
}
