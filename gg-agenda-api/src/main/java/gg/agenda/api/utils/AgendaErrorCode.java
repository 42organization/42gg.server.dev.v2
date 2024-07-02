package gg.agenda.api.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgendaErrorCode {
	AGENDA_NOT_FOUND(404, "Agenda not found");

	private final int code;

	private final String message;
}
