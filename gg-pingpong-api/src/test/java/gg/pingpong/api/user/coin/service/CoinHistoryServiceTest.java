package gg.pingpong.api.user.coin.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.store.service.CoinHistoryService;
import gg.pingpong.data.manage.CoinPolicy;
import gg.pingpong.data.store.CoinHistory;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.coin.CoinHistoryRepository;
import gg.pingpong.repo.coin.CoinPolicyRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@IntegrationTest
@RequiredArgsConstructor
@Transactional
class CoinHistoryServiceTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	CoinHistoryService coinHistoryService;

	@Autowired
	CoinHistoryRepository coinHistoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CoinPolicyRepository coinPolicyRepository;

	@BeforeEach
	void beforeEach() {
		CoinPolicy coinPolicy = CoinPolicy.builder()
			.user(testDataUtils.createAdminUser())
			.attendance(1)
			.normal(3)
			.rankWin(10)
			.rankLose(5)
			.build();
		coinPolicyRepository.save(coinPolicy);
	}

	@Test
	@DisplayName("출석 재화이력 등록")
	void addAttendanceCoinHistory() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

		coinHistoryService.addAttendanceCoinHistory(user);

		List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

		assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

		System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
		for (CoinHistory ch : afterCoinHistory) {
			System.out.println("이력 : " + ch.getHistory());
			System.out.println("사용값 : " + ch.getAmount());
			System.out.println("사용자 : " + ch.getUser().getIntraId());
			System.out.println("생성날짜 : " + ch.getCreatedAt());
		}
	}

	@Test
	@DisplayName("일반게임 재화이력 등록")
	void addNormalCoin() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

		coinHistoryService.addNormalCoin(user);

		List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

		assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

		System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
		for (CoinHistory ch : afterCoinHistory) {
			System.out.println("이력 : " + ch.getHistory());
			System.out.println("사용값 : " + ch.getAmount());
			System.out.println("사용자 : " + ch.getUser().getIntraId());
			System.out.println("생성날짜 : " + ch.getCreatedAt());
		}

	}

	@Test
	@DisplayName("랭크게임 win 재화이력 등록")
	void addRankWinCoin() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

		coinHistoryService.addRankWinCoin(user);

		List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

		assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

		System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
		for (CoinHistory ch : afterCoinHistory) {
			System.out.println("이력 : " + ch.getHistory());
			System.out.println("사용값 : " + ch.getAmount());
			System.out.println("사용자 : " + ch.getUser().getIntraId());
			System.out.println("생성날짜 : " + ch.getCreatedAt());
		}
	}

	@Test
	@DisplayName("랭크게임 lose 재화이력 등록")
	void addRankLoseCoin() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

		coinHistoryService.addRankLoseCoin(user);
		coinHistoryService.addRankLoseCoin(user);

		List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

		assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 2);

		System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
		for (CoinHistory ch : afterCoinHistory) {
			System.out.println("이력 : " + ch.getHistory());
			System.out.println("사용값 : " + ch.getAmount());
			System.out.println("사용자 : " + ch.getUser().getIntraId());
			System.out.println("생성날짜 : " + ch.getCreatedAt());
		}
	}
}
