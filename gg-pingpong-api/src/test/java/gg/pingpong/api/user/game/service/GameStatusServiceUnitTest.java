package gg.pingpong.api.user.game.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.noti.service.SnsNotiService;
import gg.pingpong.api.user.user.service.UserService;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.manage.SlotManagement;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.out.GameUser;
import gg.pingpong.repo.manage.SlotManagementRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.game.GameDataConsistencyException;
import gg.pingpong.utils.exception.match.SlotNotFoundException;

@UnitTest
class GameStatusServiceUnitTest {
	@Mock
	GameRepository gameRepository;
	@Mock
	SnsNotiService snsNotiService;
	@Mock
	NotiService notiService;
	@Mock
	UserService userService;
	@Mock
	SlotManagementRepository slotManagementRepository;
	@InjectMocks
	GameStatusService gameStatusService;

	List<Game> gameList;
	List<GameUser> gameUserList;

	@BeforeEach
	void beforeEach() {
		gameList = new ArrayList<>(List.of(mock(Game.class), mock(Game.class), mock(Game.class)));
		gameUserList = new ArrayList<>();
	}

	@Nested
	@DisplayName("updateBeforeToLiveStatus 메서드 유닛 테스트")
	class UpdateBeforeToLiveStatus {
		@BeforeEach
		void beforeEach() {
			// given
			given(gameRepository.findAllByStatusAndStartTimeLessThanEqual(any(), any()))
				.willReturn(gameList);
		}

		@Test
		@DisplayName("success")
		void success() {
			// when, then
			gameStatusService.updateBeforeToLiveStatus();
			verify(gameRepository, times(1)).findAllByStatusAndStartTimeLessThanEqual(any(), any());
		}
	}

	@Nested
	@DisplayName("updateLiveToWaitStatus 메서드 유닛 테스트")
	class UpdateLiveToWaitStatus {
		@BeforeEach
		void beforeEach() {
			// given
			given(gameRepository.findAllByStatusAndEndTimeLessThanEqual(any(), any()))
				.willReturn(gameList);
		}

		@Test
		@DisplayName("success")
		void success() {
			// when, then
			gameStatusService.updateLiveToWaitStatus();
			verify(gameRepository, times(1)).findAllByStatusAndEndTimeLessThanEqual(any(), any());
		}
	}

	@Nested
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("imminentGame 메서드 유닛 테스트")
	class ImminentGame {
		@BeforeEach
		void beforeEach() {
			// given
			given(slotManagementRepository.findCurrent(any())).willReturn(Optional.of(mock(SlotManagement.class)));
			given(gameRepository.findAllByStartTimeLessThanEqual(any())).willReturn(gameUserList);
		}

		@ParameterizedTest
		@ValueSource(ints = {0, 2})
		@DisplayName("success")
		void success(int size) {
			// given
			for (int i = 0; i < size; i++) {
				gameUserList.add(mock(GameUser.class));
			}
			// when, then
			gameStatusService.imminentGame();
			verify(slotManagementRepository, times(1)).findCurrent(any());
			verify(gameRepository, times(1)).findAllByStartTimeLessThanEqual(any());
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 3})
		@DisplayName("GameDataConsistencyException")
		void gameDataConsistencyException(int size) {
			// given
			for (int i = 0; i < size; i++) {
				gameUserList.add(mock(GameUser.class));
			}
			// when, then
			assertThatThrownBy(() -> gameStatusService.imminentGame())
				.isInstanceOf(GameDataConsistencyException.class);
		}

		@Test
		@DisplayName("SlotNotFoundException")
		void slotNotFoundException() {
			// given
			given(slotManagementRepository.findCurrent(any())).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> gameStatusService.imminentGame())
				.isInstanceOf(SlotNotFoundException.class);
		}
	}
}
