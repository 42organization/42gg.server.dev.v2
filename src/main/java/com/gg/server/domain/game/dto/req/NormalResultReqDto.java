package com.gg.server.domain.game.dto.req;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class NormalResultReqDto {
    @NotNull(message = "gameId 는 필수 값입니다.")
    @Positive(message = "gameId 는 양수만 입력 가능합니다.")
    private Long gameId;
    @NotNull(message = "myTeamId 는 필수 값입니다.")
    @Positive(message = "myTeamId 는 양수만 입력 가능합니다.")
    private Long myTeamId;
    @NotNull(message = "enemyTeamId 는 필수 값입니다.")
    @Positive(message = "enemyTeamId 는 양수만 입력 가능합니다.")
    private Long enemyTeamId;
}
