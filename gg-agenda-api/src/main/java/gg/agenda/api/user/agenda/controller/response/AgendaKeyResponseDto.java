package gg.agenda.api.user.agenda.controller.response;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaKeyResponseDto {

	private UUID agendaKey;

	@Builder
	public AgendaKeyResponseDto(UUID agendaKey) {
		this.agendaKey = agendaKey;
	}
}
