package gg.agenda.api.user.agenda.controller.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaAwardsReqDto {

	@Valid
	@NotNull
	@NotEmpty
	private List<AgendaTeamAward> awards;

	@Builder
	public AgendaAwardsReqDto(List<AgendaTeamAward> awards) {
		this.awards = awards;
	}
}
