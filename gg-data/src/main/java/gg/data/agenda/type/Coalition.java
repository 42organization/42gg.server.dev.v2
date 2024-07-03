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
}
