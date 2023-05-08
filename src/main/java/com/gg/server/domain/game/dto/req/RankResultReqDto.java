package com.gg.server.domain.game.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@AllArgsConstructor
public class RankResultReqDto {

    @Positive
    @NotNull(message = "gameId 는 필수 값입니다.")
    private Long gameId;
    @NotNull(message = "myTeamId 는 필수 값입니다.")
    @Positive
    private Long myTeamId;
    @NotNull(message = "myTeamScore 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int myTeamScore;
    @NotNull(message = "enemyTeamId 는 필수 값입니다.")
    @Positive
    private Long enemyTeamId;
    @NotNull(message = "enemyTeamScore 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int enemyTeamScore;
}
