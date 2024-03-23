package gg.pingpong.api.admin.match.controller.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gg.pingpong.api.admin.match.service.dto.MatchUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledMatchesResponseDto {
	private List<EnrolledMatch> matches = new ArrayList<>();

	public EnrolledMatchesResponseDto(List<EnrolledMatch> matches) {
		this.matches = matches;
	}

	public static EnrolledMatchesResponseDto of(Map<LocalDateTime, List<MatchUser>> matchesMap, int interval) {
		EnrolledMatchesResponseDto enrolledMatchesResponseDto = new EnrolledMatchesResponseDto();
		List<EnrolledMatch> matches1 = enrolledMatchesResponseDto.matches;

		for (Map.Entry<LocalDateTime, List<MatchUser>> entry : matchesMap.entrySet()) {
			matches1.add(EnrolledMatch.of(entry.getKey(), entry.getValue(), interval));
		}
		return enrolledMatchesResponseDto;
	}
}
