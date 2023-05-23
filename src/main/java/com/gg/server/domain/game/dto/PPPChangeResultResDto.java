package com.gg.server.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class PPPChangeResultResDto extends ExpChangeResultResDto {
    private Integer changedPpp;
    private Integer beforePpp;

    public PPPChangeResultResDto(Integer beforeExp, Integer currentExp, Integer beforePpp, Integer afterPpp) {
        super(beforeExp, currentExp);
        this.changedPpp = afterPpp - beforePpp;
        this.beforePpp = beforePpp;
    }
}
