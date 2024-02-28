package gg.pingpong.api.global.utils;

public class ExpLevelCalculator {
	private static Integer expPerGame = 100;
	private static Integer expBonus = 10;
	private static int[] accumulatedExpForEachLevel = {
		0, 100, 200, 300, 500, 700, 900, 1200, 1500, 1800,
		2200, 2600, 3000, 3400, 3800, 4300, 4800, 5300, 5800, 6300,
		6900, 7500, 8100, 8700, 9300, 10000, 10700, 11400, 12100, 12800,
		13600, 14400, 15200, 16000, 16800, 17700, 18600, 19500, 20400, 21300,
		22300, 25100, Integer.MAX_VALUE
	};

	private static int[] expForEachLevel = {
		0, 100, 100, 100, 200, 200, 200, 300, 300, 300,
		400, 400, 400, 400, 400, 500, 500, 500, 500, 500,
		600, 600, 600, 600, 600, 700, 700, 700, 700, 700,
		800, 800, 800, 800, 800, 900, 900, 900, 900, 900,
		1000, 2800, Integer.MAX_VALUE
	};

	public static int getLevel(int totalExp) {
		int idx = 0;
		while (totalExp >= accumulatedExpForEachLevel[idx]) {
			++idx;
		}
		return idx;
	}

	public static int getCurrentLevelMyExp(int totalExp) {
		int level = getLevel(totalExp);
		return totalExp - accumulatedExpForEachLevel[level - 1];
	}

	public static int getLevelMaxExp(int level) {
		return expForEachLevel[level];
	}

	public static int getExpPerGame() {
		return expPerGame;
	}

	public static int getExpBonus() {
		return expBonus;
	}
}
