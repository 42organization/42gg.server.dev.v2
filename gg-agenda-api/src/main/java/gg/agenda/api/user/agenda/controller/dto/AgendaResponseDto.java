package gg.agenda.api.user.agenda.controller.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AgendaResponseDto {

	private String agendaTitle;

	private String agendaContents;

	private String agendaDeadLine;

	private String agendaStartTime;

	private String agendaEndTime;

	private int agendaMinTeam;

	private int agendaMaxTeam;

	private int agendaCurrentTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	private String agendaPoster;

	private String agendaHost;

	private String agendaLocation;

	private String agendaStatus;

	private String createdAt;

	private boolean isOfficial;

	private String announcementTitle;

	@Builder
	public AgendaResponseDto(String agendaTitle, String agendaContents, String agendaDeadLine, String agendaStartTime,
		String agendaEndTime, int agendaMinTeam, int agendaMaxTeam, int agendaCurrentTeam, int agendaMinPeople,
		int agendaMaxPeople, String agendaPoster, String agendaHost, String agendaLocation, String agendaStatus,
		String createdAt, String announcementTitle, boolean isOfficial) {
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

		AgendaResponseDto.MapStruct INSTANCE = Mappers.getMapper(AgendaResponseDto.MapStruct.class);

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
		@Mapping(target = "isOfficial", source = "agenda.official")
		@Mapping(target = "announcementTitle", source = "announcement.title")
		AgendaResponseDto toDto(Agenda agenda, AgendaAnnouncement announcement);
	}
}
