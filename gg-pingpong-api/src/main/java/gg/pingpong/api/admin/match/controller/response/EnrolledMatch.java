package gg.pingpong.api.admin.match.controller.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import gg.pingpong.api.admin.match.service.dto.MatchUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledMatch {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private List<MatchUser> waitList = new ArrayList<>();

	public static EnrolledMatch of(LocalDateTime startTime, List<MatchUser> waitList, int interval) {
		EnrolledMatch enrolledMatch = new EnrolledMatch();
		enrolledMatch.startTime = startTime;
		enrolledMatch.endTime = startTime.plusMinutes(interval);
		enrolledMatch.waitList.addAll(waitList);
		return enrolledMatch;
	}
}
