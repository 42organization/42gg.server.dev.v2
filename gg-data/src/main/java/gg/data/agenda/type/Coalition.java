package gg.data.agenda.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Coalition {
	GUN("GUN"),
	GON("GON"),
	GAM("GAM"),
	LEE("LEE"),
	SPRING("SPRING"),
	SUMMER("SUMMER"),
	AUTUMN("AUTUMN"),
	WINTER("WINTER"),
	OTHER("OTHER");

	private String coalition;

	public static Coalition valueOfCoalition(String coalition) {
		String coalitionToUpper = coalition.toUpperCase();
		for (Coalition c : values()) {
			if (c.coalition.equals(coalitionToUpper)) {
				return c;
			}
		}
		return OTHER;
	}
}
