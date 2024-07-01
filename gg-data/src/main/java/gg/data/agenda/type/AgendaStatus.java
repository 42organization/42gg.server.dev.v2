package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgendaStatus {
	CANCEL("CANCEL"),
	ON_GOING("ON_GOING"),
	CONFIRM("CONFIRM");

	private final String status;
}
