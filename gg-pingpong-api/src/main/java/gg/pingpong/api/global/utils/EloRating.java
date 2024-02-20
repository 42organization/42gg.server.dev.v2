package gg.pingpong.api.global.utils;

public class EloRating {
	public static Integer pppChange(Integer myPPP, Integer opponentPPP, Boolean isWin, Boolean isOneSide) {
		Double we = 1.0 / (Math.pow(10.0, (opponentPPP - myPPP) / 400.0) + 1.0);
		Double change = 40 * ((isWin ? 1 : 0) - we);
		if (isOneSide) {
			change = change + change * 0.21;
		}
		if (change < 0) {
			change = change * 0.9;
		}
		return change.intValue();
	}
}
