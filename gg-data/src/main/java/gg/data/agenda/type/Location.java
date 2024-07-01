package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Location {
	SEOUL("SEOUL"),
	GYEONGSAN("GYEONGSAN"),
	MIX("MIX");

	private String location;
}
