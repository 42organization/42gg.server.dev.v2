package com.gg.server.domain.team.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamReqDto {

    @NotNull
    private Long teamId;

    @NotNull
    @Max(value = 2, message = "점수는 최대 2점 입니다.")
    @Min(value = 0, message = "점수는 최소 0점 입니다.")
    private int score;

    public TeamReqDto(Long teamId, int score) {
        this.teamId = teamId;
        this.score = score;
    }

    @Override
    public String toString() {
        return "TeamReqDto{" +
                "teamId=" + teamId +
                ", score=" + score +
                '}';
    }
}
