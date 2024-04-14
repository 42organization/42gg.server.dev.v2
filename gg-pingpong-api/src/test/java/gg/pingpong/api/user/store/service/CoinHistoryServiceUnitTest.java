package gg.pingpong.api.user.store.service;

import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.data.pingpong.store.CoinHistory;
import gg.data.pingpong.store.CoinPolicy;
import gg.data.pingpong.store.Item;
import gg.data.pingpong.store.type.HistoryType;
import gg.data.user.User;
import gg.repo.store.CoinHistoryRepository;
import gg.repo.store.CoinPolicyRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.coin.CoinPolicyNotFoundException;

@UnitTest
@DisplayName("CoinHistoryServiceUnitTest")
class CoinHistoryServiceUnitTest {
	@Mock
	CoinHistoryRepository coinHistoryRepository;
	@Mock
	CoinPolicyRepository coinPolicyRepository;
	@InjectMocks
	CoinHistoryService coinHistoryService;

	@Nested
	@DisplayName("addAttendanceCoinHistory 메서드 unitTest")
	class AddAttendanceCoinHistory {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(coinPolicy));
			//when
			coinHistoryService.addAttendanceCoinHistory(user);
			//then
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}

		@Test
		@DisplayName("CoinPolicyNotFoundException")
		void coinPolicyNotFoundException() {
			//given
			User user = mock(User.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> coinHistoryService.addAttendanceCoinHistory(user))
				.isInstanceOf(CoinPolicyNotFoundException.class);
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}
	}

	@Nested
	@DisplayName("addPurchaseItemCoinHistory 메서드 unitTest")
	class AddPurchaseItemCoinHistory {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			Item item = mock(Item.class);
			int price = 100;
			//when
			coinHistoryService.addPurchaseItemCoinHistory(user, item, price);
		}
	}

	@Nested
	@DisplayName("addGiftItemCoinHistory 메서드 unitTest")
	class AddGiftItemCoinHistory {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			User target = mock(User.class);
			Item item = mock(Item.class);
			int price = 100;
			//when
			coinHistoryService.addGiftItemCoinHistory(user, target, item, price);
		}
	}

	@Nested
	@DisplayName("addNormalCoin 메서드 unitTest")
	class AddNormalCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(coinPolicy));
			//when
			coinHistoryService.addNormalCoin(user);
			//then
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}

		@Test
		@DisplayName("CoinPolicyNotFoundException")
		void coinPolicyNotFoundException() {
			//given
			User user = mock(User.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> coinHistoryService.addNormalCoin(user))
				.isInstanceOf(CoinPolicyNotFoundException.class);
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}
	}

	@Nested
	@DisplayName("addRankWinCoin 메서드 unitTest")
	class AddRankWinCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(coinPolicy));
			//when
			coinHistoryService.addRankWinCoin(user);
			//then
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}

		@Test
		@DisplayName("CoinPolicyNotFoundException")
		void coinPolicyNotFoundException() {
			//given
			User user = mock(User.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> coinHistoryService.addRankWinCoin(user))
				.isInstanceOf(CoinPolicyNotFoundException.class);
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}
	}

	@Nested
	@DisplayName("addRankLoseCoin 메서드 unitTest")
	class AddRankLoseCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(coinPolicy));
			//when
			coinHistoryService.addRankLoseCoin(user);
			//then
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}

		@Test
		@DisplayName("CoinPolicyNotFoundException")
		void coinPolicyNotFoundException() {
			//given
			User user = mock(User.class);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> coinHistoryService.addRankLoseCoin(user))
				.isInstanceOf(CoinPolicyNotFoundException.class);
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
		}
	}

	@Nested
	@DisplayName("hasAttendedToday 메서드 unitTest")
	class HasAttendedToday {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			//when
			coinHistoryService.hasAttendedToday(user);
		}
	}

	@Nested
	@DisplayName("addCoinHistory 메서드 unitTest")
	class AddCoinHistory {
		@Test
		@DisplayName("success")
		void success() {
			//given
			CoinHistory coinHistory = mock(CoinHistory.class);
			//when
			coinHistoryService.addCoinHistory(coinHistory);
			//then
			verify(coinHistoryRepository).save(coinHistory);
		}
	}
}
