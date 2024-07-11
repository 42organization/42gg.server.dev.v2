package gg.agenda.api.user.agenda.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamAwardDto {
	private String teamName;

	private String awardName;

	private Integer awardPriority;

	@Builder
	public AgendaTeamAwardDto(String teamName, String awardName, Integer awardPriority) {
		this.teamName = teamName;
		this.awardName = awardName;
		this.awardPriority = awardPriority;
	}
}
