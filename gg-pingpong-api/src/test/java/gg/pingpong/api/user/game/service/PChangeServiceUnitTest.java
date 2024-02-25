package gg.pingpong.api.user.game.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.PChange;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.PChangeRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.pchange.PChangeNotExistException;

@UnitTest
class PChangeServiceUnitTest {
	@Mock
	PChangeRepository pChangeRepository;
	@InjectMocks
	PChangeService pChangeService;
	List<PChange> pChangeList;

	@BeforeEach
	void beforeEach() {
		int size = 5;
		pChangeList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			pChangeList.add(mock(PChange.class));
		}
	}

	@Nested
	@DisplayName("addPChange 매서드 유닛 테스트")
	class AddPChange {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(pChangeRepository.save(any())).willReturn(mock(PChange.class));
			// when, then
			pChangeService.addPChange(mock(Game.class), mock(User.class), 1, true);
		}
	}

	@Nested
	@DisplayName("findExpChangeHistory 매서드 유닛 테스트")
	class FindExpChangeHistory {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(pChangeRepository.findExpHistory(any(), any())).willReturn(pChangeList);
			// when, then
			pChangeService.findExpChangeHistory(1L, 2L);
		}

		@Test
		@DisplayName("PChangeNotExistException")
		void notExistException() {
			// given
			given(pChangeRepository.findExpHistory(any(), any())).willReturn(new ArrayList<>());
			// when, then
			assertThatThrownBy(() -> pChangeService.findExpChangeHistory(1L, 2L))
				.isInstanceOf(PChangeNotExistException.class);

		}
	}

	@Nested
	@DisplayName("findPPPChangeHistory 매서드 유닛 테스트")
	class FindPPPChangeHistory {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(pChangeRepository.findPPPHistory(any(), any(), any())).willReturn(pChangeList);
			// when, then
			pChangeService.findPPPChangeHistory(any(), any(), any());
		}

		@Test
		@DisplayName("PChangeNotExistException")
		void notExistException() {
			// given
			given(pChangeRepository.findPPPHistory(any(), any(), any())).willReturn(new ArrayList<>());
			// when, then
			assertThatThrownBy(() -> pChangeService.findPPPChangeHistory(any(), any(), any()))
				.isInstanceOf(PChangeNotExistException.class);
		}

	}
}
