package gg.agenda.api.user.controller.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gg.data.agenda.Agenda;
import lombok.Builder;
import lombok.Data;

@Data
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

	private String announcementTitle;

	private boolean isOfficial;

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
	public interface MapStruct{

		AgendaResponseDto.MapStruct INSTANCE = Mappers.getMapper(AgendaResponseDto.MapStruct.class);

		AgendaResponseDto toDto(Agenda agenda);
	}
}
