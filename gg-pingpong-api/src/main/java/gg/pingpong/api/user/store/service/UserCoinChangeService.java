package gg.pingpong.api.user.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.game.Team;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.store.Item;
import gg.data.user.User;
import gg.pingpong.api.user.game.service.GameFindService;
import gg.pingpong.api.user.store.dto.UserGameCoinResultDto;
import gg.repo.store.CoinPolicyRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.coin.CoinPolicyNotFoundException;
import gg.utils.exception.user.UserAlreadyAttendanceException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCoinChangeService {
	private final CoinPolicyRepository coinPolicyRepository;
	private final CoinHistoryService coinHistoryService;
	private final UserRepository userRepository;
	private final GameFindService gameFindService;

	/**
	 * 출석 코인을 제공합니다.
	 * @param user 유저
	 * @exception UserAlreadyAttendanceException 당일에 이미 출석을 한 경우
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 * @return 코인 증가량
	 */
	@Transactional
	public int addAttendanceCoin(User user) {
		if (coinHistoryService.hasAttendedToday(user)) {
			throw new UserAlreadyAttendanceException();
		}
		int coinIncrement = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new).getAttendance();
		user.addGgCoin(coinIncrement);
		coinHistoryService.addAttendanceCoinHistory(user);
		return coinIncrement;
	}

	/**
	 * 유저가 코인을 사용하여 아이템을 구매합니다.
	 * @param item 아이템
	 * @param price 가격
	 * @param userId 유저 아이디
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void purchaseItemCoin(Item item, Integer price, Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		user.payGgCoin(price);

		coinHistoryService.addPurchaseItemCoinHistory(user, item, price);
	}

	/**
	 * 아이템 선물 후 코인 사용 내역을 저장합니다.
	 * @param item 아이템
	 * @param price 가격
	 * @param user 유저
	 * @param giftTarget 선물 받을 유저
	 */
	@Transactional
	public void giftItemCoin(Item item, Integer price, User user, User giftTarget) {
		user.payGgCoin(price);

		coinHistoryService.addGiftItemCoinHistory(user, giftTarget, item, price);
	}

	/**
	 * 일반 게임 종료 후 코인을 부여합니다.
	 * @param userId 유저 아이디
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 * @exception CoinPolicyNotFoundException 코인 정책이 존재하지 않을 경우
	 * @return
	 */
	@Transactional
	public UserGameCoinResultDto addNormalGameCoin(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		int coinIncrement = coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(CoinPolicyNotFoundException::new).getNormal();

		user.addGgCoin(coinIncrement);
		coinHistoryService.addNormalCoin(user);
		return new UserGameCoinResultDto(user.getGgCoin(), coinIncrement);
	}

	/**
	 * 랭크 게임 종료 후 코인을 부여합니다.
	 * @param gameId 게임 아이디
	 * @param userId 유저 아이디
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 * @return
	 */
	@Transactional
	public UserGameCoinResultDto addRankGameCoin(Long gameId, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		int coinIncrement;

		if (userIsWinner(gameId, user)) {
			coinIncrement = coinHistoryService.addRankWinCoin(user);
		} else {
			coinIncrement = coinHistoryService.addRankLoseCoin(user);
		}

		user.addGgCoin(coinIncrement);
		return new UserGameCoinResultDto(user.getGgCoin(), coinIncrement);
	}

	private boolean userIsWinner(Long gameId, User user) {
		List<Team> teams = gameFindService.findByGameId(gameId).getTeams();

		for (Team team : teams) {
			for (TeamUser teamUser : team.getTeamUsers()) {
				if (teamUser.getUser().getId() == user.getId() && team.getWin()) {
					return true;
				} else if (teamUser.getUser().getId() == user.getId() && !team.getWin()) {
					return false;
				}
			}
		}

		return false;
	}
}
