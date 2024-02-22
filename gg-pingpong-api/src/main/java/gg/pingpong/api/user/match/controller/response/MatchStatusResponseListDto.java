package gg.pingpong.api.user.match.controller.response;

import java.util.List;

import gg.pingpong.api.user.match.dto.MatchStatusDto;
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
