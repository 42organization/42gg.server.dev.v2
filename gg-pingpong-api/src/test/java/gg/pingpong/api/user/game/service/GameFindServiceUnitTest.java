package gg.pingpong.api.user.game.service;

import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import gg.pingpong.data.game.Game;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.utils.annotation.UnitTest;

@UnitTest
class GameFindServiceUnitTest {
	@Mock
	GameRepository gameRepository;
	@InjectMocks
	GameFindService gameFindService;

	List<Long> gameIdList;
	List<Game> gameList;

	@BeforeEach
	void beforeEach() {
		int size = 5;
		gameIdList = new ArrayList<>();
		for (long i = 0; i < size; i++) {
			gameIdList.add(i);
		}
		gameList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			gameList.add(mock(Game.class));
		}
	}

	@Nested
	@DisplayName("normalGameListByIntra 매서드 유닛 테스트")
	class NormalGameListByIntra {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findGamesByUserAndModeAndStatus(any(), any(), any(), any()))
				.willReturn(new SliceImpl<>(gameIdList));
			// when, then
			gameFindService.normalGameListByIntra(mock(Pageable.class), "intraId");
		}
	}

	@Nested
	@DisplayName("getNormalGameList 매서드 유닛 테스트")
	class GetNormalGameList {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findAllByModeAndStatus(any(), any(), any()))
				.willReturn(new SliceImpl<>(gameList));
			// when, then
			gameFindService.getNormalGameList(mock(Pageable.class));
		}
	}

	@Nested
	@DisplayName("rankGameListByIntra 매서드 유닛 테스트")
	class RankGameListByIntra {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findGamesByUserAndModeAndSeason(any(), any(), any(), any(), any()))
				.willReturn(new SliceImpl<>(gameIdList));
			// when, then
			gameFindService.rankGameListByIntra(mock(Pageable.class), 1L, "intraId");
		}
	}

	@Nested
	@DisplayName("rankGameList 매서드 유닛 테스트")
	class RankGameList {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findAllByModeAndStatusAndSeasonId(any(), any(), any(), any()))
				.willReturn(new SliceImpl<>(gameList));
			// when, then
			gameFindService.rankGameList(mock(Pageable.class), 1L);
		}
	}

	@Nested
	@DisplayName("allGameList 매서드 유닛 테스트")
	@MockitoSettings(strictness = Strictness.LENIENT)
	class AllGameList {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findAllByModeInAndStatusIn(anyList(), anyList(), any()))
				.willReturn(new SliceImpl<>(gameList));
			given(gameRepository.findAllByModeInAndStatus(anyList(), any(), any()))
				.willReturn(new SliceImpl<>(gameList));
			// when, then
			gameFindService.allGameList(mock(Pageable.class), "LIVE");
			gameFindService.allGameList(mock(Pageable.class), "END");
		}
	}

	@Nested
	@DisplayName("allGameListUser 매서드 유닛 테스트")
	class AllGameListUser {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findGamesByUserAndModeInAndStatusIn(any(), anyList(), anyList(), any()))
				.willReturn(new SliceImpl<>(gameIdList));
			// when, then
			gameFindService.allGameListUser(mock(Pageable.class), "intraId", "END");
			gameFindService.allGameListUser(mock(Pageable.class), "intraId", "LIVE");

		}
	}

	@Nested
	@DisplayName("findByGameId 매서드 유닛 테스트")
	class FindByGameId {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findById(any())).willReturn(Optional.of(mock(Game.class)));
			// when, then
			gameFindService.findByGameId(1L);
		}
	}

	@Nested
	@DisplayName("findGameWithPessimisticLockById 매서드 유닛 테스트")
	class FindGameWithPessimisticLockById {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(gameRepository.findWithPessimisticLockById(any())).willReturn(Optional.of(mock(Game.class)));
			// when, then
			gameFindService.findGameWithPessimisticLockById(1L);
		}
	}
}
