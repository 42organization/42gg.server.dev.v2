package com.gg.server.admin.game.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class RankGamePPPModifyReqDto {
    @NotNull(message = "Team1Id 는 필수 값입니다.")
    @Positive
    private Long team1Id;
    @NotNull(message = "Team1Score 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int team1Score;
    @NotNull(message = "Team2Id 는 필수 값입니다.")
    @Positive
    private Long team2Id;
    @NotNull(message = "Team2Score 는 필수 값입니다.")
    @PositiveOrZero
    @Max(2)
    private int team2Score;
}
