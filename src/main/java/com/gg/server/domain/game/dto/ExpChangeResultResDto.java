package com.gg.server.domain.game.dto;

import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
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
    private int beforeCoin;
    private int afterCoin;
    private int coinIncrement;

    public ExpChangeResultResDto(Integer beforeExp, Integer currentExp, UserGameCoinResultDto userGameCoinResultDto) {
        this.beforeExp = ExpLevelCalculator.getCurrentLevelMyExp(beforeExp);
        this.beforeLevel = ExpLevelCalculator.getLevel(beforeExp);
        this.beforeMaxExp = ExpLevelCalculator.getLevelMaxExp(beforeLevel);
        this.increasedExp = currentExp - beforeExp;
        this.increasedLevel = ExpLevelCalculator.getLevel(currentExp) - this.beforeLevel;
        this.afterMaxExp = ExpLevelCalculator.getLevelMaxExp(this.beforeLevel + this.increasedLevel);
        this.beforeCoin = userGameCoinResultDto.getBeforeCoin();
        this.afterCoin = userGameCoinResultDto.getAfterCoin();
        this.coinIncrement = userGameCoinResultDto.getCoinIncrement();
    }

    //랭크 게임 수정 후 사라질 생성자
    public ExpChangeResultResDto(Integer beforeExp, Integer currentExp) {
        this.beforeExp = ExpLevelCalculator.getCurrentLevelMyExp(beforeExp);
        this.beforeLevel = ExpLevelCalculator.getLevel(beforeExp);
        this.beforeMaxExp = ExpLevelCalculator.getLevelMaxExp(beforeLevel);
        this.increasedExp = currentExp - beforeExp;
        this.increasedLevel = ExpLevelCalculator.getLevel(currentExp) - this.beforeLevel;
        this.afterMaxExp = ExpLevelCalculator.getLevelMaxExp(this.beforeLevel + this.increasedLevel);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ExpChangeResultResDto)) {
            return false;
        } else {
            ExpChangeResultResDto other = (ExpChangeResultResDto) obj;
            return this.beforeExp.equals(other.getBeforeExp())
                    && this.beforeLevel.equals(other.getBeforeLevel())
                    && this.beforeMaxExp.equals(other.getBeforeMaxExp())
                    && this.increasedExp.equals(other.getIncreasedExp())
                    && this.increasedLevel.equals(other.getIncreasedLevel())
                    && this.afterMaxExp.equals(other.getAfterMaxExp());
        }
    }
}
