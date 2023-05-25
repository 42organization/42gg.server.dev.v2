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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof PPPChangeResultResDto)) {
            return false;
        } else {
            PPPChangeResultResDto other = (PPPChangeResultResDto) obj;
            return this.changedPpp.equals(other.getChangedPpp())
                    && this.beforePpp.equals(other.getBeforePpp());
        }
    }
}
