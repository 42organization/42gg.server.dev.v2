package gg.agenda.api.user.agenda.controller.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaConfirmRequestDto {

	List<AgendaTeamAwardDto> awards;

	@Builder
	public AgendaConfirmRequestDto(List<AgendaTeamAwardDto> awards) {
		this.awards = awards;
	}
}
