package com.gg.server.domain.coin.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.exception.CoinPolicyNotFoundException;
import com.gg.server.domain.coin.type.HistoryType;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.user.data.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
	private final CoinHistoryRepository coinHistoryRepository;
	private final CoinPolicyRepository coinPolicyRepository;

	@Transactional
	public void addAttendanceCoinHistory(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getAttendance();
		addCoinHistory(new CoinHistory(user, HistoryType.ATTENDANCECOIN.getHistory(), amount));
	}

	@Transactional
	public void addPurchaseItemCoinHistory(User user, Item item, Integer price) {
		addCoinHistory(new CoinHistory(user, item.getName() + " 구매", price * (-1)));
	}

	@Transactional
	public void addGiftItemCoinHistory(User user, User giftTarget, Item item, Integer price) {
		addCoinHistory(new CoinHistory(user, giftTarget.getIntraId() + "에게 " + item.getName() + " 선물", price * (-1)));
	}

	@Transactional
	public void addNormalCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getNormal();
		addCoinHistory(new CoinHistory(user, HistoryType.NORMAL.getHistory(), amount));
	}

	@Transactional
	public int addRankWinCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getRankWin();
		addCoinHistory(new CoinHistory(user, HistoryType.RANKWIN.getHistory(), amount));
		return amount;
	}

	@Transactional
	public int addRankLoseCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getRankLose();
		if (amount == 0) {
			return amount;
		}
		addCoinHistory(new CoinHistory(user, HistoryType.RANKLOSE.getHistory(), amount));
		return amount;
	}

	@Transactional(readOnly = true)
	public boolean hasAttendedToday(User user) {
		LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		return coinHistoryRepository.existsUserAttendedCheckToday(
			user, HistoryType.ATTENDANCECOIN.getHistory(), startOfDay, endOfDay);
	}

	public void addCoinHistory(CoinHistory coinHistory) {
		coinHistoryRepository.save(coinHistory);
	}

}
