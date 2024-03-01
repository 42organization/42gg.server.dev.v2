package gg.pingpong.api.admin.match.controller.response;

// import gg.pingpong.data.match.type.Option;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUser {
	private Long userId;
	private String intraId;
	// private Option mode;

	// public MatchUser(Long userId, String intraId, Option mode) {
	// 	this.userId = userId;
	// 	this.intraId = intraId;
	// 	// this.mode = mode;
	// }
}
