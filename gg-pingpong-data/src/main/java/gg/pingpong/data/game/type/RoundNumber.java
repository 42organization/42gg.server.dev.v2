package gg.pingpong.data.game.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoundNumber {
	THE_FINAL(2, "결승"),
	SEMI_FINAL(4, "4강"),
	QUARTER_FINAL(8, "8강");

	private final int round;
	private final String code;
}
