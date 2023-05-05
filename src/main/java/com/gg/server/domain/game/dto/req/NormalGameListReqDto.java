package com.gg.server.domain.game.dto.req;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class NormalGameListReqDto {
    @PositiveOrZero
    @NotNull(message = "pageNum 은 필수 값입니다.")
    private Integer pageNum;
    @Positive
    @NotNull(message = "pageSize 는 필수 값입니다.")
    private Integer pageSize;

    public NormalGameListReqDto(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
