package gg.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameInfoDto {
	private Long gameId;
	private Long myTeamId;
	private Long myUserId;
	private Long enemyTeamId;
	private Long enemyUserId;
}
