package gg.pingpong.api.user.coin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserGameCoinResultDto {
	private int beforeCoin;
	private int afterCoin;
	private int coinIncrement;

	public UserGameCoinResultDto(int afterCoin, int coinIncrement) {
		this.beforeCoin = afterCoin - coinIncrement;
		this.afterCoin = afterCoin;
		this.coinIncrement = coinIncrement;
	}
}
