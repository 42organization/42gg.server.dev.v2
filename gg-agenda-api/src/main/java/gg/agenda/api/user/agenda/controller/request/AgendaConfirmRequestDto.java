package gg.agenda.api.user.agenda.controller.request;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import gg.utils.exception.custom.InvalidParameterException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaConfirmRequestDto {

	@NotNull
	@NotEmpty
	private List<AgendaTeamAwardDto> awards;

	@Builder
	public AgendaConfirmRequestDto(List<AgendaTeamAwardDto> awards) {
		this.awards = awards;
	}
}
