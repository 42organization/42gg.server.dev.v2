package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Location {
	SEOUL("SEOUL"),
	GYEONGSAN("GYEONGSAN"),
	MIX("MIX");

	private final String location;

	public static Location valueOfLocation(String location) {
		String locationToUpper = location.toUpperCase();
		for (Location l : values()) {
			if (l.location.equals(locationToUpper)) {
				return l;
			}
		}
		return MIX;
	}

	public static boolean isUnderLocation(Location criteria, Location target) {
		if (criteria == MIX) {
			return true;
		}
		return criteria == target;
	}
}
