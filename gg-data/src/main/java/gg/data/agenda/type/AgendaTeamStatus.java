package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgendaTeamStatus {
	OPEN("OPEN"),
	CANCEL("CANCEL"),
	CONFIRM("CONFIRM");

	private String status;
}
