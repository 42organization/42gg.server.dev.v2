package com.gg.server.domain.tier.service;

import static org.mockito.Mockito.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class TierServiceUnitTest {

	@Mock
	TierRepository tierRepository;
	@Mock
	RankRepository rankRepository;
	@InjectMocks
	TierService tierService;

	@Nested
	@DisplayName("UpdateAllTier")
	class UpdateAllTier {
		List<Tier> tiers;

		Season season;

		@BeforeEach
		void init() {
			tiers = IntStream.range(0, 7).mapToObj((i) -> mock(Tier.class)).collect(Collectors.toList());
			when(tierRepository.findAll(any(Sort.class))).thenReturn(tiers);
			season = mock(Season.class);
			when(season.getId()).thenReturn(1L);
		}

		@ParameterizedTest
		@DisplayName("랭크 게임 미참여시 0번 티어")
		@ValueSource(ints = {1, 3, 5, 7})
		@MockitoSettings(strictness = Strictness.LENIENT)
		void notParticipant(int size) {
			//Arrange
			List<Rank> rankList = IntStream.range(0, size)
				.mapToObj((i) -> new Rank(mock(User.class), season, 1000, 0, 0, "", mock(Tier.class)))
				.collect(Collectors.toList());
			when(rankRepository.findAllBySeasonIdOrderByPppDesc(any())).thenReturn(rankList);
			when(rankRepository.countRealRankPlayers(any())).thenReturn(0L);

			//Act
			tierService.updateAllTier(season);

			//Assert
			Assertions.assertThat(rankList).allMatch((rank) -> rank.getTier().equals(tiers.get(0)));
		}

		@ParameterizedTest
		@DisplayName("참여한 유저 중 ppp 최대 3명 6번 티어")
		@ValueSource(ints = {1, 3, 5, 7})
		@MockitoSettings(strictness = Strictness.LENIENT)
		void top3(int size) {
			//Arrange
			List<Rank> rankList = IntStream.range(0, size)
				.mapToObj((i) -> new Rank(mock(User.class), season, 900 + 10 * i, 1, 0, "", mock(Tier.class)))
				.sorted(Comparator.comparing(Rank::getPpp).reversed())
				.collect(Collectors.toList());
			when(rankRepository.findAllBySeasonIdOrderByPppDesc(any())).thenReturn(rankList);
			when(rankRepository.countRealRankPlayers(any())).thenReturn((long)size);

			//Act
			tierService.updateAllTier(season);
			long cnt = rankList.stream().filter((rank) -> rank.getTier().equals(tiers.get(6))).count();

			//Assert
			Assertions.assertThat(cnt).isEqualTo(Math.min(3, size));
		}

	}

}
