package gg.agenda.api.admin.agenda.controller.request;

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

	@NotBlank
	private String agendaTitle;

	@NotBlank
	private String agendaContents;

	private String agendaPoster;

	@NotNull
	private Boolean isOfficial;

	@NotNull
	private Boolean isRanking;

	@NotNull
	private AgendaStatus agendaStatus;

	@NotNull
	private LocalDateTime agendaDeadLine;

	@NotNull
	private LocalDateTime agendaStartTime;

	@NotNull
	private LocalDateTime agendaEndTime;

	@NotNull
	private Location agendaLocation;

	@Min(2)
	@Max(1000)
	private int agendaMinTeam;

	@Min(2)
	@Max(1000)
	private int agendaMaxTeam;

	@Min(1)
	@Max(100)
	private int agendaMinPeople;

	@Min(1)
	@Max(100)
	private int agendaMaxPeople;

	@Builder
	public AgendaAdminUpdateReqDto(String agendaTitle, String agendaContents, String agendaPoster, Boolean isOfficial,
		Boolean isRanking, AgendaStatus agendaStatus, LocalDateTime agendaDeadLine, LocalDateTime agendaStartTime,
		LocalDateTime agendaEndTime, Location agendaLocation, int agendaMinTeam, int agendaMaxTeam,
		int agendaMinPeople, int agendaMaxPeople) {
		this.agendaTitle = agendaTitle;
		this.agendaContents = agendaContents;
		this.agendaPoster = agendaPoster;
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
