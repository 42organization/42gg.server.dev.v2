package gg.pingpong.api.user.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttendanceResponseDto {
	private int beforeCoin;
	private int afterCoin;
	private int coinIncrement;
}
