package com.gg.server.data.user.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RacketType {
	PENHOLDER("PENHOLDER"),
	SHAKEHAND("SHAKEHAND"),
	DUAL("DUAL"),
	NONE("NONE");

	private final String code;

	@JsonCreator
	public static RacketType getEnumFromValue(String value) {
		for (RacketType e : values()) {
			if (e.code.equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}

}
