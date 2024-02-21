package gg.pingpong.api.domain.coin.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.Season;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.manage.CoinPolicy;
import com.gg.server.data.store.Item;
import com.gg.server.data.store.type.ItemType;
import com.gg.server.data.user.User;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.coin.exception.CoinHistoryNotFoundException;
import com.gg.server.domain.coin.exception.CoinPolicyNotFoundException;
import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@IntegrationTest
@RequiredArgsConstructor
@Transactional
class UserCoinChangeServiceTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	CoinHistoryService coinHistoryService;

	@Autowired
	CoinHistoryRepository coinHistoryRepository;

	@Autowired
	CoinPolicyRepository coinPolicyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserCoinChangeService userCoinChangeService;

	@Autowired
	ItemRepository itemRepository;

	User admin;

	@BeforeEach
	void init() {
		admin = testDataUtils.createAdminUser();
	}

	@Test
	@DisplayName("출석시 재화 증가 서비스 테스트")
	void addAttendanceCoin() {
		CoinPolicy coinPolicy = CoinPolicy.builder().user(admin).attendance(100).build();
		coinPolicyRepository.save(coinPolicy);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		int beforeCoin = user.getGgCoin();

		int coinIncrement = userCoinChangeService.addAttendanceCoin(user);

		assertThat(beforeCoin + coinIncrement).isEqualTo(user.getGgCoin());
		assertThat(coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getAttendance()).isEqualTo(coinIncrement);
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());

		try {
			coinIncrement = userCoinChangeService.addAttendanceCoin(user);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e);
			System.out.println("===출석 중복 제거 기능 수행 완료===");
		}
	}

	@Test
	@DisplayName("아이템 구매시 코인사용 테스트")
	void purchaseItemCoin() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);
		user.addGgCoin(100);
		int beforeCoin = user.getGgCoin();

		Item item = new Item("과자", "111", "1", "1", 100, true, 0,
			ItemType.EDGE, LocalDateTime.now(), user.getIntraId());
		itemRepository.save(item);

		userCoinChangeService.purchaseItemCoin(item, item.getPrice(), userId);

		assertThat(beforeCoin).isEqualTo(user.getGgCoin() + item.getPrice());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory()
			+ coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getAmount());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());
		try {
			userCoinChangeService.purchaseItemCoin(item, item.getPrice(), userId);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e);
			System.out.println("===coin이 없을 때 방어로직 기능 수행 완료===");
		}
	}

	@Test
	@DisplayName("아이템 선물시 코인사용 테스트")
	void giftItemCoin() {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);
		user.addGgCoin(100);
		int beforeCoin = user.getGgCoin();

		Item item = new Item("과자", "111", "1", "1", 100, true, 0,
			ItemType.EDGE, LocalDateTime.now(), user.getIntraId());
		itemRepository.save(item);

		userCoinChangeService.giftItemCoin(item, item.getPrice(), user, user);

		assertThat(beforeCoin).isEqualTo(user.getGgCoin() + item.getPrice());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory()
			+ coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getAmount());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());

		try {
			userCoinChangeService.giftItemCoin(item, item.getPrice(), user, user);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e);
			System.out.println("===coin이 없을 때 방어로직 기능 수행 완료===");
		}
	}

	@Test
	@DisplayName("노말 게임 재화 증가 서비스 테스트")
	void addNormalGameService() {
		CoinPolicy coinPolicy = CoinPolicy.builder().user(admin).normal(100).build();
		coinPolicyRepository.save(coinPolicy);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addNormalGameCoin(userId);

		assertThat(user.getGgCoin()).isEqualTo(userGameCoinResultDto.getAfterCoin());
		assertThat(coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getNormal()).isEqualTo(
			userGameCoinResultDto.getCoinIncrement());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());
	}

	@Test
	@DisplayName("랭크 게임  승리 재화 증가 서비스 테스트")
	void addRankWinGameService() {
		CoinPolicy coinPolicy = CoinPolicy.builder().user(admin).rankWin(100).build();
		coinPolicyRepository.save(coinPolicy);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		Season season = testDataUtils.createSeason();
		Game mockMatch = testDataUtils.createMockMatch(user, season,
			LocalDateTime.now().minusMinutes(20),
			LocalDateTime.now().minusMinutes(5), Mode.RANK, 2, 1);
		UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService
			.addRankGameCoin(mockMatch.getId(), user.getId()); //본인의 게임Id와 id 값

		assertThat(user.getGgCoin()).isEqualTo(userGameCoinResultDto.getAfterCoin());
		assertThat(coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getRankWin())
			.isEqualTo(userGameCoinResultDto.getCoinIncrement());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());
	}

	@Test
	@DisplayName("랭크 게임 패배 재화 증가 서비스 테스트")
	void addRankLoseGameService() {
		CoinPolicy coinPolicy = CoinPolicy.builder().user(admin).rankLose(100).build();
		coinPolicyRepository.save(coinPolicy);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);

		Season season = testDataUtils.createSeason();
		Game mockMatch = testDataUtils.createMockMatch(user, season,
			LocalDateTime.now().minusMinutes(20),
			LocalDateTime.now().minusMinutes(5), Mode.RANK, 1, 2);

		UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService
			.addRankGameCoin(mockMatch.getId(), user.getId());

		assertThat(user.getGgCoin()).isEqualTo(userGameCoinResultDto.getAfterCoin());
		assertThat(coinPolicyRepository.findTopByOrderByCreatedAtDesc()
			.orElseThrow(() -> new CoinPolicyNotFoundException()).getRankLose())
			.isEqualTo(userGameCoinResultDto.getCoinIncrement());
		System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new CoinHistoryNotFoundException()).getHistory());
	}
}
