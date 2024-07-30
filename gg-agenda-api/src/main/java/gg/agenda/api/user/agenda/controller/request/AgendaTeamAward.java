package gg.agenda.api.user.agenda.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaTeamAward {

	@NotNull
	@NotEmpty
	private String teamName;

	@NotBlank
	@Length(max = 30)
	private String awardName;

	@Min(1)
	@Max(1000)
	private int awardPriority;

	@Builder
	public AgendaTeamAward(String teamName, String awardName, int awardPriority) {
		this.teamName = teamName;
		this.awardName = awardName;
		this.awardPriority = awardPriority;
	}
}
