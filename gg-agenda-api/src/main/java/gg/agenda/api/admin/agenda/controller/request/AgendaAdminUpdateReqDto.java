package gg.agenda.api.admin.agenda.controller.request;

import java.net.URL;
import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

	private String agendaContents;

	private URL agendaPoster;

	private Boolean isOfficial;

	private Boolean isRanking;

	private AgendaStatus agendaStatus;

	private LocalDateTime agendaDeadLine;

	private LocalDateTime agendaStartTime;

	private LocalDateTime agendaEndTime;

	private Location agendaLocation;

	private int agendaMinTeam;

	private int agendaMaxTeam;

	private int agendaMinPeople;

	private int agendaMaxPeople;

	@Builder
	public AgendaAdminUpdateReqDto(String agendaTitle, String agendaContents, URL agendaPosterUri, Boolean isOfficial,
		Boolean isRanking, AgendaStatus agendaStatus, LocalDateTime agendaDeadLine, LocalDateTime agendaStartTime,
		LocalDateTime agendaEndTime, Location agendaLocation, int agendaMinTeam, int agendaMaxTeam,
		int agendaMinPeople, int agendaMaxPeople) {
		this.agendaTitle = agendaTitle;
		this.agendaContents = agendaContents;
		this.agendaPoster = agendaPosterUri;
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
