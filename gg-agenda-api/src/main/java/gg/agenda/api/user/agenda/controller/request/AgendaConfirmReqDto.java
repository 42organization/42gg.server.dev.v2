package gg.agenda.api.user.agenda.controller.request;

import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaConfirmReqDto {

	@Valid
	@NotNull
	@NotEmpty
	private List<AgendaTeamAwardDto> awards;

	@Builder
	public AgendaConfirmReqDto(List<AgendaTeamAwardDto> awards) {
		this.awards = awards;
	}

	public static Map<String, AgendaAward> toMap(List<AgendaTeamAwardDto> agendaTeamAwards) {
		return agendaTeamAwards.stream().collect(Collectors.toMap(AgendaTeamAwardDto::getTeamName,
			agendaTeamAwardDto -> AgendaAward.builder()
				.awardName(agendaTeamAwardDto.getAwardName())
				.awardPriority(agendaTeamAwardDto.getAwardPriority())
				.build()));
	}
}
