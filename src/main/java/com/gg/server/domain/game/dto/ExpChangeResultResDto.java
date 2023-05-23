package com.gg.server.domain.game.dto;

import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExpChangeResultResDto {
    private Integer beforeExp;
    private Integer beforeMaxExp;
    private Integer beforeLevel;
    private Integer increasedExp;
    private Integer increasedLevel;
    private Integer afterMaxExp;

    public ExpChangeResultResDto(Integer beforeExp, Integer currentExp) {
        this.beforeExp = beforeExp;
        this.beforeLevel = ExpLevelCalculator.getLevel(beforeExp);
        this.beforeMaxExp = ExpLevelCalculator.getLevelMaxExp(beforeLevel);
        this.increasedExp = currentExp - beforeExp;
        this.increasedLevel = ExpLevelCalculator.getLevel(currentExp) - this.beforeLevel;
        this.afterMaxExp = ExpLevelCalculator.getLevelMaxExp(this.beforeLevel + this.increasedLevel);
    }
}
