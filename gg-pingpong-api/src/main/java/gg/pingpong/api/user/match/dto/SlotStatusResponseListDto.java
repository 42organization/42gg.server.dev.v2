package gg.pingpong.api.user.match.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlotStatusResponseListDto {
	private List<List<SlotStatusDto>> matchBoards;

	@Override
	public String toString() {
		return "MatchStatusResponseListDto{"
			+ "matchBoards=" + matchBoards
			+ '}';
	}

	public SlotStatusResponseListDto(List<List<SlotStatusDto>> matchBoards) {
		this.matchBoards = matchBoards;
	}
}
