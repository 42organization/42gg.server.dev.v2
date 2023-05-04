package com.gg.server.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class RankGameListReqDto extends GameListReqDto {
    @Positive
    @NotNull(message = "seasonId 는 필수 값입니다.")
    private Long seasonId;

    public RankGameListReqDto(@PositiveOrZero @NotNull(message = "count 는 필수 값입니다.") Integer count, @Positive @NotNull(message = "pageSize 는 필수 값입니다.") Integer pageSize, Long seasonId) {
        super(count, pageSize);
        this.seasonId = seasonId;
    }
}
