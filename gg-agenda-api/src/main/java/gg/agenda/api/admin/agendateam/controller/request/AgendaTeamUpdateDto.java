package gg.agenda.api.admin.agendateam.controller.request;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamUpdateDto {

	@NotNull
	private UUID teamKey;

	@NotBlank
	@Size(max = 30)
	private String teamName;

	@NotBlank
	private String teamContent;

	@NotNull
	private AgendaTeamStatus teamStatus;

	@NotNull
	private Boolean teamIsPrivate;

	@NotNull
	private Location teamLocation;

	@NotNull
	private String teamAward;

	@Min(1)
	@Max(1000)
	private Integer teamAwardPriority;

	@Valid
	@NotNull
	private List<AgendaTeamMateReqDto> teamMates;

	@Builder
	public AgendaTeamUpdateDto(UUID teamKey, String teamName, String teamContent, AgendaTeamStatus teamStatus,
		Location teamLocation, String teamAward, Integer teamAwardPriority, Boolean teamIsPrivate,
		List<AgendaTeamMateReqDto> teamMates) {
		this.teamKey = teamKey;
		this.teamName = teamName;
		this.teamContent = teamContent;
		this.teamStatus = teamStatus;
		this.teamAward = teamAward;
		this.teamAwardPriority = teamAwardPriority;
		this.teamIsPrivate = teamIsPrivate;
		this.teamLocation = teamLocation;
		this.teamMates = teamMates;
	}
}
