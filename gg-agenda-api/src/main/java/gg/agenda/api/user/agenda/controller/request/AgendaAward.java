package gg.agenda.api.user.agenda.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAward {

	private String awardName;

	private int awardPriority;

	@Builder
	public AgendaAward(String awardName, int awardPriority) {
		this.awardName = awardName;
		this.awardPriority = awardPriority;
	}
}
