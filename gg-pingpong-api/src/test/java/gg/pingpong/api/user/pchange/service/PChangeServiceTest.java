package gg.pingpong.api.user.pchange.service;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.utils.ReflectionUtilsForUnitTest;
import gg.pingpong.api.utils.annotation.UnitTest;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.PChange;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.data.user.type.SnsType;
import gg.pingpong.repo.pchange.PChangeRepository;
import gg.pingpong.utils.exception.pchange.PChangeNotExistException;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PChangeServiceTest {
	@Mock
	private PChangeRepository pChangeRepository;

	@InjectMocks
	private PChangeService pChangeService;

	@Test
	@DisplayName("addPChange Success Test")
	public void testAddPChange() {
		Game game = new Game();
		User user = new User("intra", "email", "image", RacketType.PENHOLDER,
			RoleType.USER, 1000, SnsType.NONE, 4242L);
		Integer pppResult = 42;
		Boolean isChecked = true;

		// given
		PChange pchange = new PChange(game, user, 4242, true);
		given(pChangeRepository.save(any(PChange.class))).willReturn(pchange);

		// when
		pChangeService.addPChange(game, user, pppResult, isChecked);

		// then
		verify(pChangeRepository, times(1)).save(any(PChange.class));
	}

	@Nested
	@DisplayName("FindExpChangeHistory Test")
	class TestFindExpChangeHistory {

		@Test
		@DisplayName("성공")
		public void successFindExpChangeHistory() {
			PChange pChange = new PChange();
			ReflectionUtilsForUnitTest.setFieldWithReflection(pChange, "game", mock(Game.class));
			ReflectionUtilsForUnitTest.setFieldWithReflection(pChange, "user", mock(User.class));

			// given
			List<PChange> lst = new ArrayList<>();
			lst.add(pChange);
			when(pChangeRepository.findExpHistory(any(Long.class), any(Long.class))).thenReturn(lst);

			// when
			pChangeService.findExpChangeHistory(1L, 1L);

			// then
			verify(pChangeRepository, times(1)).findExpHistory(1L, 1L);
		}

		@Test
		@DisplayName("실패 : nowExistGameId")
		public void pchangeNotExistExceptionTest() {
			Long gameId = 1L;
			Long userId = 1L;

			// given
			when(pChangeRepository.findExpHistory(userId, gameId)).thenReturn(new ArrayList<>());

			// when, then
			assertThrows(PChangeNotExistException.class, () -> pChangeService.findExpChangeHistory(gameId, userId));

		}

	}

	@Nested
	@DisplayName("FindPPPChangeHistory Test")
	class TestFindPPPChangeHistory {

		@Test
		@DisplayName("성공")
		public void success() {
			Long gameId = 1L;
			Long userId = 1L;
			Long seasonId = 1L;

			PChange pChange = new PChange();
			ReflectionUtilsForUnitTest.setFieldWithReflection(pChange, "game", mock(Game.class));
			ReflectionUtilsForUnitTest.setFieldWithReflection(pChange, "user", mock(User.class));

			List<PChange> pChangeList = new ArrayList<>();
			pChangeList.add(pChange);
			// given
			when(pChangeRepository.findPPPHistory(userId, gameId, seasonId)).thenReturn(pChangeList);

			// when
			pChangeService.findPPPChangeHistory(gameId, userId, seasonId);

			// then
			verify(pChangeRepository, times(1)).findPPPHistory(userId, gameId, seasonId);
		}

		@Test
		@DisplayName("실패 : notExist")
		public void notExist() {
			Long gameId = 1L;
			Long userId = 1L;
			Long seasonId = 1L;

			// given
			when(pChangeRepository.findPPPHistory(userId, gameId, seasonId)).thenReturn(new ArrayList<>());

			// when, then
			assertThrows(PChangeNotExistException.class, () -> {
				pChangeService.findPPPChangeHistory(gameId, userId, seasonId);
			});
		}
	}
}
