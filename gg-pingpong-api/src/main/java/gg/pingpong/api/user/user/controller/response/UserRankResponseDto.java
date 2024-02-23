package gg.pingpong.api.user.user.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserRankResponseDto {
	private int rank;
	private int ppp;
	private int wins;
	private int losses;
	private double winRate;
}
