package gg.pingpong.api.user.user.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserCoinResponseDto {
	private int coin;

	public UserCoinResponseDto(int userCoin) {
		this.coin = userCoin;
	}
}
