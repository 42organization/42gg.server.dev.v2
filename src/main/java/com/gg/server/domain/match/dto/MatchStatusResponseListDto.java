package com.gg.server.domain.match.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MatchStatusResponseListDto {
	private List<MatchStatusDto> match;

	@Override
	public String toString() {
		return "MatchStatusResponseListDto{"
			+ "match=" + match
			+ '}';
	}

	public MatchStatusResponseListDto(List<MatchStatusDto> match) {
		this.match = match;
	}
}
