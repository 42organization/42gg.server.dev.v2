package gg.agenda.api.admin.agenda.controller.request;

import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAdminUpdateReqDto {

	private String agendaTitle;

	private String agendaContent;

	private URL agendaPosterUri;

	private Boolean isOfficial;

	private Boolean isRanking;

	private AgendaStatus agendaStatus;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime agendaDeadLine;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime agendaStartTime;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime agendaEndTime;

	private Location agendaLocation;

	private int agendaMinTeam;

	private int agendaMaxTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	@Builder
	public AgendaAdminUpdateReqDto(String agendaTitle, String agendaContent, URL agendaPosterUri, Boolean isOfficial,
		Boolean isRanking, AgendaStatus agendaStatus, LocalDateTime agendaDeadLine, LocalDateTime agendaStartTime,
		LocalDateTime agendaEndTime, Location agendaLocation, int agendaMinTeam, int agendaMaxTeam,
		int agendaMinPeople, int agendaMaxPeople) {
		this.agendaTitle = agendaTitle;
		this.agendaContent = agendaContent;
		this.agendaPosterUri = agendaPosterUri;
		this.isOfficial = isOfficial;
		this.isRanking = isRanking;
		this.agendaStatus = agendaStatus;
		this.agendaDeadLine = agendaDeadLine;
		this.agendaStartTime = agendaStartTime;
		this.agendaEndTime = agendaEndTime;
		this.agendaLocation = agendaLocation;
		this.agendaMinTeam = agendaMinTeam;
		this.agendaMaxTeam = agendaMaxTeam;
		this.agendaMinPeople = agendaMinPeople;
		this.agendaMaxPeople = agendaMaxPeople;
	}
}
