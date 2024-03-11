package gg.pingpong.api.admin.match.service.dto;

import gg.data.match.RedisMatchUser;
import gg.data.match.type.Option;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUser {
	private Long userId;
	private String intraId;
	private Integer ppp;
	private Option option;

	@Builder
	public MatchUser(Long userId, String intraId, Integer ppp, Option option) {
		this.userId = userId;
		this.intraId = intraId;
		this.ppp = ppp;
		this.option = option;
	}

	public static MatchUser of(RedisMatchUser user, String intraId) {
		return MatchUser.builder()
			.userId(user.getUserId())
			.intraId(intraId)
			.ppp(user.getPpp())
			.option(user.getOption())
			.build();
	}
}
