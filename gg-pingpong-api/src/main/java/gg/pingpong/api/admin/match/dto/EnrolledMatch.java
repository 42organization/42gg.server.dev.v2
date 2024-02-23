package gg.pingpong.api.admin.match.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledMatch {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Boolean isMatched;
	private List<MatchUser> players;

	public EnrolledMatch(LocalDateTime startTime, LocalDateTime endTime, Boolean isMatched, List<MatchUser> players) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.isMatched = isMatched;
		this.players = players;
	}
}
