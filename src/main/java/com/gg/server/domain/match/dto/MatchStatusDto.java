package com.gg.server.domain.match.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gg.server.data.game.Game;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.match.RedisMatchTime;
import com.gg.server.domain.slotmanagement.SlotManagement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchStatusDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime startTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime endTime;
	private Boolean isMatched;
	private Boolean isImminent;
	private List<String> myTeam;
	private List<String> enemyTeam;

	public MatchStatusDto(Game game, String userIntraId, String enemyIntraId, SlotManagement slotManagement) {
		this.startTime = game.getStartTime();
		this.endTime = game.getEndTime();
		this.isMatched = true;
		this.isImminent = game.getStartTime().minusMinutes(slotManagement.getOpenMinute())
			.isBefore(LocalDateTime.now());
		if (game.getMode() == Mode.TOURNAMENT) {
			isImminent = true;
		}
		this.myTeam = List.of(userIntraId);
		this.enemyTeam = List.of(enemyIntraId);

	}

	public MatchStatusDto(RedisMatchTime redisMatchTime, Integer interval) {
		this.startTime = redisMatchTime.getStartTime();
		this.endTime = redisMatchTime.getStartTime().plusMinutes(interval);
		this.isMatched = false;
		this.isImminent = false;
		this.myTeam = List.of();
		this.enemyTeam = List.of();
	}

	@Override
	public String toString() {
		return "CurrentMatchResponseDto{"
			+ "startTime=" + startTime
			+ "endTIme=" + endTime
			+ ", myTeam=" + myTeam
			+ ", enemyTeam=" + enemyTeam
			+ ", isMatched=" + isMatched
			+ '}';
	}
}
