package gg.pingpong.api.user.tier.service;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

import gg.pingpong.api.user.rank.service.TierService;
import gg.pingpong.data.game.Rank;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.tier.TierRepository;
import gg.pingpong.utils.annotation.UnitTest;

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
				.mapToObj((i) -> new Rank(mock(User.class), season, 900 + 10 * i, 0, 1, "", mock(Tier.class)))
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

		/**
		 * 개선 필요: 최상위 3명이 기본 ppp보다 낮을 경우 6번 티어가 0명이 된다.
		 * 게임상 재현은 불가능하지만 코드상 버그로 판단.
		 */
		@ParameterizedTest
		@DisplayName("전체 티어 조건 검증")
		@ValueSource(ints = {100})
		@MockitoSettings(strictness = Strictness.LENIENT)
		void allTierCondition(int size) {
			//Arrange
			ArrayList<Rank> rankArrayList = IntStream.range(0, size)
				.mapToObj((i) -> new Rank(mock(User.class), season, 900 + 10 * i, 1, 0, "", mock(Tier.class)))
				.collect(Collectors.toCollection(ArrayList::new));
			when(rankRepository.countRealRankPlayers(any())).thenReturn((long)size);

			IntStream.range(0, 100)
				.mapToObj((i) -> new Rank(mock(User.class), season, 1000, 0, 0, "", mock(Tier.class)))
				.forEach(rankArrayList::add);
			when(rankRepository.countRealRankPlayers(any())).thenReturn((long)size);

			List<Rank> rankList = rankArrayList.stream()
				.sorted(Comparator.comparing(Rank::getPpp).reversed())
				.collect(Collectors.toList());
			when(rankRepository.findAllBySeasonIdOrderByPppDesc(any())).thenReturn(rankList);

			//Act
			tierService.updateAllTier(season);

			List<Rank> tier0 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(0)))
				.collect(Collectors.toList());

			List<Rank> tier1 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(1)))
				.collect(Collectors.toList());

			List<Rank> tier2 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(2)))
				.collect(Collectors.toList());

			List<Rank> tier3 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(3)))
				.collect(Collectors.toList());

			List<Rank> tier4 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(4)))
				.sorted(Comparator.comparing(Rank::getPpp))
				.collect(Collectors.toList());
			Integer tier4MinPPP = tier4.get(0).getPpp();

			List<Rank> tier5 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(5)))
				.sorted(Comparator.comparing(Rank::getPpp))
				.collect(Collectors.toList());
			Integer tier5MinPPP = tier5.get(0).getPpp();

			List<Rank> tier6 = rankList.stream()
				.filter((rank) -> rank.getTier().equals(tiers.get(6)))
				.sorted(Comparator.comparing(Rank::getPpp))
				.collect(Collectors.toList());
			Integer tier6MinPPP = tier6.get(0).getPpp();

			//Assert
			Assertions.assertThat(tier0).allMatch((rank) -> rank.getWins() == 0 && rank.getLosses() == 0);
			Assertions.assertThat(tier1).allMatch((rank) -> rank.getPpp() < 970);
			Assertions.assertThat(tier2).allMatch((rank) -> rank.getPpp() < 1010);
			Assertions.assertThat(tier3).allMatch((rank) -> rank.getPpp() < tier4MinPPP);
			Assertions.assertThat(tier4).allMatch((rank) -> rank.getPpp() < tier5MinPPP);
			Assertions.assertThat(tier5).allMatch((rank) -> rank.getPpp() <= tier6MinPPP);
			Assertions.assertThat(tier6.size()).isEqualTo(Math.min(3, size));
		}
	}

}
