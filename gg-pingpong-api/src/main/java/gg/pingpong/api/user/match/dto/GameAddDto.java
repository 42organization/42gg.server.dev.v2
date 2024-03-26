package gg.pingpong.api.user.match.dto;

import java.time.LocalDateTime;
import java.util.List;

import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.match.RedisMatchUser;
import gg.data.pingpong.match.type.Option;
import gg.data.pingpong.season.Season;
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
		this.mode = ofPlayers(List.of(player, enemy), season.getPppGap());
	}

	public Mode ofPlayers(List<RedisMatchUser> players, Integer pppGap) {
		if (players.stream().allMatch(player -> player.getOption().equals(Option.BOTH))) {
			if (Math.abs(players.get(0).getPpp() - players.get(1).getPpp()) <= pppGap) {
				return Mode.RANK;
			}
			return Mode.NORMAL;
		}
		if (!players.get(0).getOption().equals(Option.BOTH)) {
			return Mode.getEnumValue(players.get(0).getOption().getCode());
		}
		return Mode.getEnumValue(players.get(1).getOption().getCode());
	}
}
