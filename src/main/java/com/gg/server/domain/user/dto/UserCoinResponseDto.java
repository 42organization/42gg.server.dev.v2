package com.gg.server.domain.user.dto;

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
