package gg.pingpong.data.tournament.type;

import static gg.pingpong.data.tournament.type.RoundNumber.*;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TournamentRound {
	// the final -> 결승
	// semi final  -> 4강
	// quarter final -> 8강
	// ordinal()로 sorting 사용되고 있으므로 순서 중요 -> 이후에 리팩토링으로 해결하겠습니다.
	THE_FINAL(null, RoundNumber.THE_FINAL, 1),
	SEMI_FINAL_1(THE_FINAL, SEMI_FINAL, 1),
	SEMI_FINAL_2(THE_FINAL, SEMI_FINAL, 2),
	QUARTER_FINAL_1(SEMI_FINAL_1, QUARTER_FINAL, 1),
	QUARTER_FINAL_2(SEMI_FINAL_1, QUARTER_FINAL, 2),
	QUARTER_FINAL_3(SEMI_FINAL_2, QUARTER_FINAL, 3),
	QUARTER_FINAL_4(SEMI_FINAL_2, QUARTER_FINAL, 4);

	private final TournamentRound nextRound;
	private final RoundNumber roundNumber;
	private final int roundOrder;

	public static List<TournamentRound> getSameRounds(RoundNumber roundNumber) {
		List<TournamentRound> sameRounds = new ArrayList<>();
		for (TournamentRound e : values()) {
			if (roundNumber.equals(e.getRoundNumber())) {
				sameRounds.add(e);
			}
		}
		return sameRounds;
	}

	/**
	 * 이전 TournamentRound의 roundNum를 반환한다.
	 * @param roundNumber - 현재 라운드
	 * @return 이전 라운드의 roundNum, 없을 경우 null 반환
	 */
	public static RoundNumber getPreviousRoundNumber(RoundNumber roundNumber) {
		for (TournamentRound e : values()) {
			if (e.nextRound != null && roundNumber == e.nextRound.getRoundNumber()) {
				return e.roundNumber;
			}
		}
		return null;
	}
}
