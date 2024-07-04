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

	public static Location valueOfLocation(String location) {
		String locationToUpper = location.toUpperCase();
		for (Location l : values()) {
			if (l.location.equals(locationToUpper)) {
				return l;
			}
		}
		return MIX;
	}
}
