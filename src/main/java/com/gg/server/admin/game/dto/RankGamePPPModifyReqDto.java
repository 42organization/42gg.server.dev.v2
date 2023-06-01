package com.gg.server.admin.game.dto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class RankGamePPPModifyReqDto {
    @NotNull(message = "TeamId1 는 필수 값입니다.")
    @Positive
    private Long Team1Id;
    @NotNull(message = "Team1Score 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int Team1Score;
    @NotNull(message = "TeamId2 는 필수 값입니다.")
    @Positive
    private Long Team2Id;
    @NotNull(message = "Team2Score 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int Team2Score;
}
