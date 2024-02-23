package gg.pingpong.api.user.game.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import gg.pingpong.api.global.utils.ExpLevelCalculator;
import gg.pingpong.api.user.store.dto.UserGameCoinResultDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@JsonInclude(Include.NON_NULL)
public class GamePChangeResultResDto {
	private Integer beforeExp;
	private Integer beforeMaxExp;
	private Integer beforeLevel;
	private Integer increasedExp;
	private Integer increasedLevel;
	private Integer afterMaxExp;
	private int beforeCoin;
	private int afterCoin;
	private int coinIncrement;
	protected Integer changedPpp;
	protected Integer beforePpp;

	public GamePChangeResultResDto(Integer beforeExp, Integer currentExp, UserGameCoinResultDto userGameCoinResultDto) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof GamePChangeResultResDto)) {
			return false;
		} else {
			GamePChangeResultResDto other = (GamePChangeResultResDto)obj;
			return this.beforeExp.equals(other.getBeforeExp())
				&& this.beforeLevel.equals(other.getBeforeLevel())
				&& this.beforeMaxExp.equals(other.getBeforeMaxExp())
				&& this.increasedExp.equals(other.getIncreasedExp())
				&& this.increasedLevel.equals(other.getIncreasedLevel())
				&& this.afterMaxExp.equals(other.getAfterMaxExp());
		}
	}
}
