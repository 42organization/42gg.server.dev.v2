package gg.pingpong.api.user.rank.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import gg.pingpong.api.user.rank.controller.response.ExpRankPageResponseDto;
import gg.pingpong.api.user.rank.controller.response.RankPageResponseDto;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.rank.RankV2Dto;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.ExpRankV2Dto;
import gg.pingpong.repo.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class RankServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private RankRedisRepository redisRepository;
	@Mock
	private SeasonFindService seasonFindService;
	@Mock
	private RankRepository rankRepository;
	@InjectMocks
	private RankService rankService;

	@BeforeEach
	void setUp() {
		// 현재 시즌 가져오기
		given(seasonFindService.findCurrentSeason(any()))
			.willReturn(Season.builder()
				.seasonName("Test Season")
				.build());
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getExpRankPage() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		// exp ranking user list, total page size 조회
		given(userRepository.findAllByTotalExpGreaterThan(pageRequest, 0))
			.willReturn(new PageImpl<>(new ArrayList<User>(), pageRequest, 1));

		// exp ranking list 조회
		List<ExpRankV2Dto> expRankV2DtoList = expRankingSampleData();
		given(userRepository.findExpRank(pageRequest.getPageNumber(), pageRequest.getPageSize(),
			null))
			.willReturn(expRankV2DtoList);
		ExpRankPageResponseDto testRes = rankService.getExpRankPage(pageRequest, UserDto
			.builder()
			.intraId("TestUser")
			.id(1L)
			.totalExp(0)
			.build());
		assertThat(testRes.getMyRank()).isEqualTo(-1);
		assertThat(testRes.getRankList().get(0).getIntraId()).isEqualTo("Test User");
	}

	private static List<ExpRankV2Dto> expRankingSampleData() {
		List<ExpRankV2Dto> expRankV2Dtos = new ArrayList<>();
		expRankV2Dtos.add(new ExpRankV2Dto() {
			@Override
			public String getIntraId() {
				return "Test User";
			}

			@Override
			public String getStatusMessage() {
				return "status message";
			}

			@Override
			public Integer getTotalExp() {
				return 0;
			}

			@Override
			public String getImageUri() {
				return "null";
			}

			@Override
			public String getTextColor() {
				return null;
			}

			@Override
			public Integer getRanking() {
				return 1;
			}
		});
		return expRankV2Dtos;
	}

	@Test
	void getRankPageV2() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		// 현재 시즌 id 에 해당하는 랭크 유저 리스트의 페이지 수
		given(rankRepository.countRankUserBySeasonId(any()))
			.willReturn(1);
		// 현재 유저 ranking
		given(rankRepository.findRankByUserIdAndSeasonId(any(), any()))
			.willReturn(Optional.of(1));
		// sample data
		List<RankV2Dto> rankV2DtoList = pppRankingSampleData();
		given(rankRepository.findPppRankBySeasonId(anyInt(), anyInt(), any()))
			.willReturn(rankV2DtoList);
		RankPageResponseDto result = rankService.getRankPageV2(pageRequest,
			UserDto
				.builder()
				.intraId("TestUser")
				.id(1L)
				.totalExp(0)
				.build(),
			any());
		assertThat(result.getCurrentPage()).isEqualTo(pageRequest.getPageNumber() + 1);
		assertThat(result.getRankList().size()).isEqualTo(rankV2DtoList.size());
	}

	private static List<RankV2Dto> pppRankingSampleData() {
		List<RankV2Dto> rankV2DtoList = new ArrayList<>();
		rankV2DtoList.add(new RankV2Dto() {
			@Override
			public String getIntraId() {
				return "TestUser";
			}

			@Override
			public String getStatusMessage() {
				return "status message";
			}

			@Override
			public Integer getPpp() {
				return 1150;
			}

			@Override
			public String getTierImageUri() {
				return "null";
			}

			@Override
			public String getTextColor() {
				return null;
			}

			@Override
			public Integer getRanking() {
				return 1;
			}
		});
		return rankV2DtoList;
	}
}
