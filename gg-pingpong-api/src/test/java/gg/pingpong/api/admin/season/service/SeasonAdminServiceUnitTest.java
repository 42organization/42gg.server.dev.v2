package gg.pingpong.api.admin.season.service;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.data.game.Season;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.season.exception.SeasonTimeBeforeException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class SeasonAdminServiceUnitTest {
	@Mock
	SeasonAdminRepository seasonAdminRepository;
	@Mock
	RankRedisAdminService rankRedisAdminService;
	@Mock
	RankAdminService rankAdminService;
	@InjectMocks
	SeasonAdminService seasonAdminService;

	int beforeSeasonCnt = 3;
	int afterSeasonCnt = 2;
	List<Season> beforeSeasons = new ArrayList<>();
	List<Season> afterSeasons = new ArrayList<>();

	@BeforeEach
	void beforeEach() {
		LocalDateTime time = LocalDateTime.now();
		beforeSeasons.clear();
		afterSeasons.clear();
		for (int i = 0; i < beforeSeasonCnt; i++) {
			beforeSeasons.add(new Season("season" + i,
				time.minusDays(i + 1).minusSeconds(1), time.minusDays(i).minusSeconds(2), 1000, 200));
		}
		for (int i = 0; i < afterSeasonCnt; i++) {
			afterSeasons.add(new Season("season" + (i + beforeSeasonCnt),
				time.plusDays(i + 3).minusSeconds(1), time.plusDays(i + 4).minusSeconds(2), 1000, 200));
		}
	}

	@Nested
	@DisplayName("findAllSeasons() 유닛 테스트")
	class FindAllSeasons {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(seasonAdminRepository.findAllByOrderByStartTimeDesc()).willReturn(beforeSeasons);
			// when, then
			seasonAdminService.findAllSeasons();
			verify(seasonAdminRepository, times(1)).findAllByOrderByStartTimeDesc();
		}
	}

	@Nested
	@DisplayName("createSeason() 유닛 테스트")
	class CreateSeason {
		SeasonCreateRequestDto requestDto;
		Season season;
		ArrayList<Season> beforeSeasonsAsc;

		@BeforeEach
		void beforeEach() {
			requestDto = new SeasonCreateRequestDto("season", LocalDateTime.now().plusDays(1).plusSeconds(1),
				1000, 200);
			season = new Season(requestDto);
			setFieldWithReflection(beforeSeasons.get(0), "id", 2L);
			beforeSeasonsAsc = new ArrayList<>(beforeSeasons);
			Collections.reverse(beforeSeasonsAsc);
		}

		@Test
		@DisplayName("success_이전시즌x&예약시즌x")
		void success1() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(new ArrayList<>());
			// when, then
			seasonAdminService.createSeason(requestDto);
			verify(seasonAdminRepository, times(1)).findBeforeSeasons(any());
			verify(seasonAdminRepository, times(1)).findAfterSeasons(any());
			verify(seasonAdminRepository, times(1)).save(any());
			verify(seasonAdminRepository, times(1)).findAllByOrderByStartTimeAsc();
			verify(rankAdminService, times(1)).addAllUserRankByNewSeason(any());
			verify(rankRedisAdminService, times(1)).addAllUserRankByNewSeason(any());
		}

		@Test
		@DisplayName("success_이전시즌o&예약시즌x")
		void success2() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(beforeSeasonsAsc);
			// when, then
			seasonAdminService.createSeason(requestDto);
			assertThat(beforeSeasons.get(0).getEndTime()).isEqualTo(requestDto.getStartTime().minusSeconds(1));
		}

		@Test
		@DisplayName("success_이전시즌x&예약시즌o")
		void success3() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(afterSeasons);
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(afterSeasons);
			// when, then
			seasonAdminService.createSeason(requestDto);
		}

		@Test
		@DisplayName("success_이전시즌o&예약시즌o")
		void success4() {
			// given
			ArrayList<Season> seasons = new ArrayList<>(beforeSeasonsAsc);
			seasons.add(season);
			setFieldWithReflection(season, "endTime", afterSeasons.get(0).getStartTime().minusSeconds(1));
			seasons.addAll(afterSeasons);
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(afterSeasons);
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(seasons);
			// when, then
			seasonAdminService.createSeason(requestDto);
		}

		@Test
		@DisplayName("isOverlap 매서드 모든조건 체크 1")
		void isOverlap1() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(beforeSeasonsAsc);
			// when, then
			beforeSeasonsAsc.get(1).setEndTime(beforeSeasonsAsc.get(1).getStartTime());
			seasonAdminService.createSeason(requestDto);
		}

		@Test
		@DisplayName("isOverlap 매서드 모든조건 체크 2")
		void isOverlap2() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(beforeSeasonsAsc);
			// when, then
			beforeSeasonsAsc.get(0).setEndTime(beforeSeasonsAsc.get(0).getStartTime());
			seasonAdminService.createSeason(requestDto);
		}

		@Test
		@DisplayName("isOverlap 매서드 모든조건 체크 3")
		void isOverlap3() {
			// given
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(beforeSeasons);
			// when, then 1
			seasonAdminService.createSeason(requestDto);
		}

		@Test
		@DisplayName("SeasonForbiddenException_1")
		void seasonForbiddenException1() {
			// given
			ArrayList<Season> seasons = new ArrayList<>(beforeSeasonsAsc);
			seasons.add(season);
			setFieldWithReflection(season, "endTime", afterSeasons.get(0).getEndTime());
			seasons.addAll(afterSeasons);
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(afterSeasons);
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			given(seasonAdminRepository.findAllByOrderByStartTimeAsc()).willReturn(seasons);
			// when, then
			assertThatThrownBy(() -> seasonAdminService.createSeason(requestDto))
				.isInstanceOf(SeasonForbiddenException.class);
		}

		@Test
		@DisplayName("SeasonForbiddenException_2")
		void seasonForbiddenException2() {
			// given
			setFieldWithReflection(beforeSeasonsAsc.get(0), "startTime", season.getStartTime().minusHours(23));
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasonsAsc);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			// when, then
			assertThatThrownBy(() -> seasonAdminService.createSeason(requestDto))
				.isInstanceOf(SeasonForbiddenException.class);
		}

		@Test
		@DisplayName("SeasonTimeBeforeException")
		void seasonTimeBeforeException() {
			// given
			setFieldWithReflection(requestDto, "startTime", LocalDateTime.now().plusHours(23).plusMinutes(59));
			// when, then
			assertThatThrownBy(() -> seasonAdminService.createSeason(requestDto))
				.isInstanceOf(SeasonTimeBeforeException.class);
		}
	}

	@Nested
	@DisplayName("findSeasonById() 유닛 테스트")
	class FindSeasonById {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(
				Optional.of(afterSeasons.get(afterSeasons.size() - 1)));
			// when, then
			seasonAdminService.findSeasonById(1L);
			verify(seasonAdminRepository, times(1)).findById(any());
		}

		@Test
		@DisplayName("SeasonNotFoundException")
		void seasonNotFoundException() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> seasonAdminService.findSeasonById(1L))
				.isInstanceOf(SeasonNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("deleteSeason() 유닛 테스트")
	class DeleteSeason {
		Season season;

		@BeforeEach
		void beforeEach() {
			season = new Season(1L, "season", LocalDateTime.now().plusDays(1).plusSeconds(1),
				LocalDateTime.of(9999, 12, 31, 23, 59, 59),
				1000, 200);
		}

		@Test
		@DisplayName("success_이전시즌x&예약시즌x")
		void success1() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			// when, then
			seasonAdminService.deleteSeason(1L);
			verify(seasonAdminRepository, times(1)).findById(any());
			verify(seasonAdminRepository, times(1)).findBeforeSeasons(any(LocalDateTime.class));
			verify(seasonAdminRepository, times(1)).findAfterSeasons(any(LocalDateTime.class));
			verify(rankAdminService, times(1)).deleteAllUserRankBySeason(any());
			verify(rankRedisAdminService, times(1)).deleteSeasonRankBySeasonId(any());
			verify(seasonAdminRepository, times(1)).delete(any());
		}

		@Test
		@DisplayName("success_이전시즌o&예약시즌x")
		void success2() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(new ArrayList<>());
			// when, then
			seasonAdminService.deleteSeason(1L);
			assertThat(beforeSeasons.get(0).getEndTime())
				.isEqualTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
		}

		@Test
		@DisplayName("success_이전시즌o&예약시즌o")
		void success3() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			given(seasonAdminRepository.findBeforeSeasons(any(LocalDateTime.class))).willReturn(beforeSeasons);
			given(seasonAdminRepository.findAfterSeasons(any(LocalDateTime.class))).willReturn(afterSeasons);
			// when, then
			seasonAdminService.deleteSeason(1L);
			assertThat(beforeSeasons.get(0).getEndTime()).isEqualTo(afterSeasons.get(0).getStartTime().minusSeconds(1));
		}

		@Test
		@DisplayName("SeasonForbiddenException")
		void seasonForbiddenException() {
			// given
			setFieldWithReflection(season, "startTime", LocalDateTime.now().minusHours(2));
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			// when, then 1
			assertThatThrownBy(() -> seasonAdminService.deleteSeason(1L))
				.isInstanceOf(SeasonForbiddenException.class);
			// when, then 1
			setFieldWithReflection(season, "endTime", LocalDateTime.now().minusHours(1));
			assertThatThrownBy(() -> seasonAdminService.deleteSeason(1L))
				.isInstanceOf(SeasonForbiddenException.class);
		}
	}

	@Nested
	@DisplayName("updateSeason() 유닛 테스트")
	class UpdateSeason {
		Season season;
		SeasonUpdateRequestDto requestDto;

		@BeforeEach
		void beforeEach() {
			season = new Season(1L, "season", LocalDateTime.now().plusDays(1).plusSeconds(1),
				LocalDateTime.of(9999, 12, 31, 23, 59, 59),
				1000, 200);
			requestDto = new SeasonUpdateRequestDto("season",
				LocalDateTime.now().plusDays(2), 1000, 500);
		}

		@Test
		@DisplayName("success")
		void success() {
			// given
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			given(seasonAdminRepository.findBeforeSeasons(any())).willReturn(new ArrayList<>());
			given(seasonAdminRepository.findAfterSeasons(any())).willReturn(new ArrayList<>());
			given(seasonAdminRepository.save(any())).willReturn(mock(Season.class));
			// when, then
			seasonAdminService.updateSeason(1L, requestDto);

			verify(seasonAdminRepository, times(2)).findById(any());
			verify(seasonAdminRepository, times(2)).findBeforeSeasons(any());
			verify(seasonAdminRepository, times(2)).findAfterSeasons(any());
			verify(seasonAdminRepository, times(1)).save(any());
			verify(rankAdminService, times(1)).deleteAllUserRankBySeason(any());
			verify(rankAdminService, times(1)).addAllUserRankByNewSeason(any());
			verify(rankRedisAdminService, times(1)).deleteSeasonRankBySeasonId(any());
			verify(rankRedisAdminService, times(1)).addAllUserRankByNewSeason(any());
		}

		@Test
		@DisplayName("SeasonForbiddenException")
		void seasonForbiddenException() {
			// given
			setFieldWithReflection(season, "startTime", LocalDateTime.now().minusHours(1));
			given(seasonAdminRepository.findById(any())).willReturn(Optional.of(season));
			// when, then
			assertThatThrownBy(() -> seasonAdminService.updateSeason(1L, requestDto))
				.isInstanceOf(SeasonForbiddenException.class);
		}
	}
}
