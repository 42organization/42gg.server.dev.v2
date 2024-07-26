package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgendaStatus {
	CANCEL("CANCEL"),
	OPEN("OPEN"),
	CONFIRM("CONFIRM"),
	FINISH("FINISH");

	private final String status;
}
