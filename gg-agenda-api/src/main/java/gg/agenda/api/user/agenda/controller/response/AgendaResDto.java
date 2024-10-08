package gg.agenda.api.user.agenda.controller.response;

import java.net.URL;
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

	private String agendaContent;

	private LocalDateTime agendaDeadLine;

	private LocalDateTime agendaStartTime;

	private LocalDateTime agendaEndTime;

	private int agendaMinTeam;

	private int agendaMaxTeam;

	private int agendaCurrentTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private String agendaPosterUrl;

	private String agendaHost;

	private Location agendaLocation;

	private AgendaStatus agendaStatus;

	private LocalDateTime createdAt;

	private Boolean isOfficial;

	private Boolean isRanking;

	private String announcementTitle;

	@Builder
	public AgendaResDto(String agendaTitle, String agendaContent, LocalDateTime agendaDeadLine,
		LocalDateTime agendaStartTime, LocalDateTime agendaEndTime, int agendaMinTeam, int agendaMaxTeam,
		int agendaCurrentTeam, int agendaMinPeople, int agendaMaxPeople, String agendaPosterUrl, String agendaHost,
		Location agendaLocation, AgendaStatus agendaStatus, LocalDateTime createdAt, String announcementTitle,
		Boolean isOfficial, Boolean isRanking) {
		this.agendaTitle = agendaTitle;
		this.agendaContent = agendaContent;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaCurrentTeam = agendaCurrentTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
		this.agendaPosterUrl = agendaPosterUrl;
		this.agendaHost = agendaHost;
		this.agendaLocation = agendaLocation;
		this.agendaStatus = agendaStatus;
		this.createdAt = createdAt;
		this.announcementTitle = announcementTitle;
		this.isOfficial = isOfficial;
		this.isRanking = isRanking;
	}

	@Mapper
	public interface MapStruct {

		AgendaResDto.MapStruct INSTANCE = Mappers.getMapper(AgendaResDto.MapStruct.class);

		@Mapping(target = "agendaTitle", source = "agenda.title")
		@Mapping(target = "agendaContent", source = "agenda.content")
		@Mapping(target = "agendaDeadLine", source = "agenda.deadline")
		@Mapping(target = "agendaStartTime", source = "agenda.startTime")
		@Mapping(target = "agendaEndTime", source = "agenda.endTime")
		@Mapping(target = "agendaMinTeam", source = "agenda.minTeam")
		@Mapping(target = "agendaMaxTeam", source = "agenda.maxTeam")
		@Mapping(target = "agendaCurrentTeam", source = "agenda.currentTeam")
		@Mapping(target = "agendaMinPeople", source = "agenda.minPeople")
		@Mapping(target = "agendaMaxPeople", source = "agenda.maxPeople")
		@Mapping(target = "agendaPosterUrl", source = "agenda.posterUri")
		@Mapping(target = "agendaHost", source = "agenda.hostIntraId")
		@Mapping(target = "agendaLocation", source = "agenda.location")
		@Mapping(target = "agendaStatus", source = "agenda.status")
		@Mapping(target = "createdAt", source = "agenda.createdAt")
		@Mapping(target = "isOfficial", source = "agenda.isOfficial")
		@Mapping(target = "isRanking", source = "agenda.isRanking")
		@Mapping(target = "announcementTitle", source = "announcementTitle")
		AgendaResDto toDto(Agenda agenda, String announcementTitle);
	}
}
