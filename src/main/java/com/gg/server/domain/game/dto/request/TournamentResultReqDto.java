package com.gg.server.domain.game.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentResultReqDto {
	@Positive
	@NotNull(message = "gameId 는 필수 값입니다.")
	private Long gameId;
	@NotNull(message = "myTeamId 는 필수 값입니다.")
	@javax.validation.constraints.Positive
	private Long myTeamId;
	@NotNull(message = "myTeamScore 는 필수 값입니다.")
	@PositiveOrZero
	@Max(2)
	private int myTeamScore;
	@NotNull(message = "enemyTeamId 는 필수 값입니다.")
	@javax.validation.constraints.Positive
	private Long enemyTeamId;
	@NotNull(message = "enemyTeamScore 는 필수 값입니다.")
	@PositiveOrZero
	@Max(2)
	private int enemyTeamScore;
}
