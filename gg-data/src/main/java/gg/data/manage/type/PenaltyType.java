package gg.data.manage.type;

import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PenaltyType {
	NONE("none"),
	NOSHOW("noshow"),
	CANCEL("cancel");

	private final String code;

	public static PenaltyType of(String code) {
		return Arrays.stream(PenaltyType.values())
			.filter(penaltyType -> penaltyType.getCode().equals(code))
			.findAny()
			.orElse(NONE);
	}

	@JsonCreator
	public static PenaltyType getEnumFromValue(String value) {
		for (PenaltyType e : values()) {
			if (e.code.equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}
}
