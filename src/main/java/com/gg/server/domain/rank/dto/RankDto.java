package com.gg.server.domain.rank.dto;

import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.user.data.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankDto {
	private String intraId;
	private int rank;
	private int ppp;
	private String statusMessage;
	private String tierImageUri;
	private String textColor;

	public static RankDto from(User user, RankRedis rankRedis, Integer rank) {
		RankDto dto = RankDto.builder()
			.intraId(user.getIntraId())
			.rank(rank)
			.ppp(rankRedis.getPpp())
			.statusMessage(rankRedis.getStatusMessage())
			.tierImageUri(rankRedis.getTierImageUri())
			.textColor(user.getTextColor())
			.build();
		return dto;
	}

	public static RankDto from(RankV2Dto dto) {
		return RankDto.builder()
			.intraId(dto.getIntraId())
			.rank(dto.getRanking())
			.ppp(dto.getPpp())
			.statusMessage(dto.getStatusMessage())
			.tierImageUri(dto.getTierImageUri())
			.textColor(dto.getTextColor())
			.build();
	}
}
