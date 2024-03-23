package gg.pingpong.api.user.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.data.game.Game;
import gg.data.game.PChange;
import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.season.Season;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.api.user.user.controller.response.UserHistoryResponseDto;
import gg.pingpong.api.utils.ReflectionUtilsForUnitTest;
import gg.repo.game.PChangeRepository;
import gg.utils.annotation.UnitTest;

@UnitTest
class UserServiceTest {

	@Mock
	SeasonFindService seasonFindService;

	@Mock
	PChangeRepository pChangeRepository;

	@InjectMocks
	UserService userService;

	@Nested
	@DisplayName("getUserHistory 테스트 - User 의 PChange 변화 내역을 조회 한다.")
	class GetUserHistoryTest {

		Season season;
		List<PChange> pChanges;
		@BeforeEach
		void setUp() {
			season = new Season();
			ReflectionUtilsForUnitTest.setFieldWithReflection(season, "id", 1L);
			pChanges = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				Game game = new Game(season, StatusType.END, Mode.RANK,
					LocalDateTime.now().minusDays(i), LocalDateTime.now().minusDays(i).plusMinutes(15));
				User user = new User("testId" + i, "testEmail" + i, "imageUri",
					RacketType.DUAL, RoleType.ADMIN, 0, SnsType.SLACK, 111L);
				PChange pChange = new PChange(game, user, 1111, true);
				ReflectionUtilsForUnitTest.setFieldWithReflection(pChange, "id", (long) i);
				pChanges.add(pChange);
			}
			when(pChangeRepository.findPChangesHistory(anyString(), anyLong())).thenReturn(pChanges);
		}

		@Test
		@DisplayName("success - seasonId가 0일 때")
		void seasonId_zero() {
			when(seasonFindService.findCurrentSeason(any())).thenReturn(season);
			UserHistoryResponseDto userHistory = userService.getUserHistory("testId", 0L);
			assertEquals(5, userHistory.getHistorics().size());
			Collections.reverse(pChanges);
			for (int i = 0; i < 5; i++) {
				assertEquals(pChanges.get(i).getGame().getStartTime(), userHistory.getHistorics().get(i).getDate());
			}
		}

		@Test
		@DisplayName("success - seasonId가 0이 아닐 때")
		void seasonId_not_zero() {
			when(seasonFindService.findSeasonById(anyLong())).thenReturn(season);
			UserHistoryResponseDto userHistory = userService.getUserHistory("testId", 1L);
			assertEquals(5, userHistory.getHistorics().size());
			Collections.reverse(pChanges);
			for (int i = 0; i < 5; i++) {
				assertEquals(pChanges.get(i).getGame().getStartTime(), userHistory.getHistorics().get(i).getDate());
			}
		}
	}
}
