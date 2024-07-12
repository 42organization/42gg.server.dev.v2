package gg.agenda.api.user.agenda.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamAwardDto {

	@NotNull
	@NotEmpty
	private String teamName;

	@NotNull
	@NotEmpty
	private String awardName;

	@Min(1)
	private int awardPriority;

	@Builder
	public AgendaTeamAwardDto(String teamName, String awardName, int awardPriority) {
		this.teamName = teamName;
		this.awardName = awardName;
		this.awardPriority = awardPriority;
	}
}
