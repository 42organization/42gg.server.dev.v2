package com.gg.server.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class GameListReqDto {
    @PositiveOrZero
    @NotNull(message = "pageNum 은 필수 값입니다.")
    Integer pageNum;
    @Positive
    @NotNull(message = "pageSize 는 필수 값입니다.")
    Integer pageSize;

    public GameListReqDto(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
