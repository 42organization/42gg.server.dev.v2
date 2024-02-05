package com.gg.server.domain.match.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.gg.server.data.game.Season;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.match.RedisMatchUser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameAddDto {
	private LocalDateTime startTime;
	private Season season;
	private Long playerId;
	private Long enemyId;
	private Mode mode;

	public GameAddDto(LocalDateTime startTime, Season season, RedisMatchUser player,
		RedisMatchUser enemy) {
		this.startTime = startTime;
		this.season = season;
		this.playerId = player.getUserId();
		this.enemyId = enemy.getUserId();
		this.mode = Mode.ofPlayers(List.of(player, enemy), season.getPppGap());
	}

}
