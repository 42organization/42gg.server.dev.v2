package gg.pingpong.api.user.store.service;

import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;
import gg.data.pingpong.store.CoinPolicy;
import gg.data.pingpong.store.Item;
import gg.data.user.User;
import gg.pingpong.api.user.game.service.GameFindService;
import gg.repo.store.CoinPolicyRepository;
import gg.repo.user.UserRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.coin.CoinPolicyNotFoundException;
import gg.utils.exception.user.UserAlreadyAttendanceException;
import gg.utils.exception.user.UserNotFoundException;

@UnitTest
@DisplayName("UserCoinChangeServiceUnitTest")
class UserCoinChangeServiceUnitTest {
	@Mock
	CoinPolicyRepository coinPolicyRepository;
	@Mock
	CoinHistoryService coinHistoryService;
	@Mock
	UserRepository userRepository;
	@Mock
	GameFindService gameFindService;
	@InjectMocks
	UserCoinChangeService userCoinChangeService;

	@Nested
	@DisplayName("addAttendanceCoin 메서드 unitTest")
	class AddAttendanceCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			CoinPolicy coinPolicy = new CoinPolicy();
			when(coinHistoryService.hasAttendedToday(user)).thenReturn(false);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(coinPolicy));
			//when
			userCoinChangeService.addAttendanceCoin(user);
			//then
			verify(coinHistoryService).hasAttendedToday(user);
			verify(coinPolicyRepository).findTopByOrderByCreatedAtDesc();
			verify(coinHistoryService).addAttendanceCoinHistory(user);
		}

		@Test
		@DisplayName("UserAlreadyAttended")
		void userAlreadyAttended() {
			//given
			User user = mock(User.class);
			when(coinHistoryService.hasAttendedToday(user)).thenReturn(true);
			//when, then
			Assertions.assertThatThrownBy(() -> userCoinChangeService.addAttendanceCoin(user))
				.isInstanceOf(UserAlreadyAttendanceException.class);
			verify(coinPolicyRepository, never()).findTopByOrderByCreatedAtDesc();
			verify(coinHistoryService, never()).addAttendanceCoinHistory(user);
		}

		@Test
		@DisplayName("CoinPolicyNotFound")
		void coinPolicyNotFound() {
			//given
			User user = mock(User.class);
			when(coinHistoryService.hasAttendedToday(user)).thenReturn(false);
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> userCoinChangeService.addAttendanceCoin(user))
				.isInstanceOf(CoinPolicyNotFoundException.class);
			verify(coinHistoryService, never()).addAttendanceCoinHistory(user);
		}
	}

	@Nested
	@DisplayName("purchaseItemCoin 메서드 unitTest")
	class PurchaseItemCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.of(mock(User.class)));
			//when
			userCoinChangeService.purchaseItemCoin(new Item(), 1, 1L);
			//then
			verify(userRepository).findById(any(Long.class));
			verify(coinHistoryService).addPurchaseItemCoinHistory(any(User.class), any(Item.class), any(Integer.class));
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			//given
			given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> userCoinChangeService.purchaseItemCoin(new Item(), 1, 1L))
				.isInstanceOf(UserNotFoundException.class);
			verify(coinHistoryService, never()).addPurchaseItemCoinHistory(any(User.class), any(Item.class),
				any(Integer.class));
		}
	}

	@Nested
	@DisplayName("giftItemCoin 메서드 unitTest")
	class GiftItemCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Item item = new Item();
			Integer price = 2;
			User user = mock(User.class);
			User targetUser = mock(User.class);
			//when
			userCoinChangeService.giftItemCoin(item, price, user, targetUser);
			//then
			verify(coinHistoryService).addGiftItemCoinHistory(user, targetUser, item, price);
		}
	}

	@Nested
	@DisplayName("addNormalGameCoin 메서드 unitTest")
	class AddNormalGameCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Long userId = 1L;
			User user = mock(User.class);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));
			when(coinPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(mock(CoinPolicy.class)));
			//when
			userCoinChangeService.addNormalGameCoin(userId);
			//then
			verify(coinHistoryService).addNormalCoin(user);
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			//given
			when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> userCoinChangeService.addNormalGameCoin(any(Long.class)))
				.isInstanceOf(UserNotFoundException.class);
			verify(coinHistoryService, never()).addNormalCoin(any(User.class));
		}
	}

	@Nested
	@DisplayName("addRankGameCoin 메서드 unitTest")
	class AddRankGameCoin {
		@Test
		@DisplayName("success")
		void success() {
			//given
			User user = mock(User.class);
			Long userId = 2L;
			Long gameId = 1L;
			when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
			when(gameFindService.findByGameId(gameId)).thenReturn(mock(Game.class));
			when(gameFindService.findByGameId(gameId).getTeams()).thenReturn(List.of(mock(Team.class)));
			//when
			userCoinChangeService.addRankGameCoin(gameId, userId);
			//then
			verify(userRepository).findById(userId);
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			//given
			Long userId = 1L;
			Long gameId = 1L;
			when(userRepository.findById(userId)).thenReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> userCoinChangeService.addRankGameCoin(gameId, userId))
				.isInstanceOf(UserNotFoundException.class);
		}
	}
}
