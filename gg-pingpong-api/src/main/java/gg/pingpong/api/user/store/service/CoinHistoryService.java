package gg.pingpong.api.user.store.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.store.CoinHistory;
import gg.data.pingpong.store.Item;
import gg.data.pingpong.store.type.HistoryType;
import gg.data.user.User;
import gg.repo.store.CoinHistoryRepository;
import gg.repo.store.CoinPolicyRepository;
import gg.utils.exception.coin.CoinPolicyNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
	private final CoinHistoryRepository coinHistoryRepository;
	private final CoinPolicyRepository coinPolicyRepository;

	/**
	 * 유저의 출석 보상 코인 이력을 추가합니다.
	 * @param user 유저
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 */
	@Transactional
	public void addAttendanceCoinHistory(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new)
			.getAttendance();
		addCoinHistory(new CoinHistory(user, HistoryType.ATTENDANCECOIN.getHistory(), amount));
	}

	/**
	 * 유저의 상품 구매 코인 이력을 추가합니다.
	 * @param user 유저
	 * @param item 아이템
	 * @param price 가격
	 */
	@Transactional
	public void addPurchaseItemCoinHistory(User user, Item item, Integer price) {
		addCoinHistory(new CoinHistory(user, item.getName() + " 구매", price * (-1)));
	}

	/**
	 * 유저의 상품 선물 코인 이력을 추가합니다.
	 * @param user 유저
	 * @param giftTarget 선물 받을 유저
	 * @param item 아이템
	 * @param price 가격
	 */
	@Transactional
	public void addGiftItemCoinHistory(User user, User giftTarget, Item item, Integer price) {
		addCoinHistory(new CoinHistory(user, giftTarget.getIntraId() + "에게 " + item.getName() + " 선물", price * (-1)));
	}

	/**
	 * 일반 게임 종료 후 코인을 추가합니다.
	 * @param user 유저
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 */
	@Transactional
	public void addNormalCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new).getNormal();
		addCoinHistory(new CoinHistory(user, HistoryType.NORMAL.getHistory(), amount));
	}

	/**
	 * 랭크 게임 우승 후 코인을 추가합니다.
	 * @param user 유저
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 * @return 추가된 코인 양
	 */
	@Transactional
	public int addRankWinCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new).getRankWin();
		addCoinHistory(new CoinHistory(user, HistoryType.RANKWIN.getHistory(), amount));
		return amount;
	}

	/**
	 * 랭크 게임 패배 후 코인을 추가합니다.
	 * @param user 유저
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 * @return 추가된 코인 양
	 */
	@Transactional
	public int addRankLoseCoin(User user) {
		int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new).getRankLose();
		if (amount == 0) {
			return amount;
		}
		addCoinHistory(new CoinHistory(user, HistoryType.RANKLOSE.getHistory(), amount));
		return amount;
	}

	/**
	 * 유저의 출석 여부를 확인합니다.
	 * 출석 코인 이력이 있는지 확인하여 출석 여부를 판단합니다.
	 * @param user 유저
	 * @return 출석 여부 (true/false)
	 */
	@Transactional(readOnly = true)
	public boolean hasAttendedToday(User user) {
		LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		return coinHistoryRepository.existsUserAttendedCheckToday(
			user, HistoryType.ATTENDANCECOIN.getHistory(), startOfDay, endOfDay);
	}

	/**
	 * 코인 이력을 추가합니다.
	 * @param coinHistory 코인 이력
	 */
	public void addCoinHistory(CoinHistory coinHistory) {
		coinHistoryRepository.save(coinHistory);
	}

}
