package com.gg.server.domain.season.service;

import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class SeasonServiceUnitTest {
	@Mock
	SeasonRepository seasonRepository;
	@InjectMocks
	SeasonService seasonService;

	@Nested
	@DisplayName("seasonList 메서드 유닛 테스트")
	class SeasonList {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(seasonRepository.findActiveSeasonsDesc(any())).willReturn(new ArrayList<>());
			// when, then
			seasonService.seasonList();
		}
	}

	@Nested
	@DisplayName("getCurSeason 메서드 유닛 테스트")
	class GetCurSeason {
		@Test
		@DisplayName("success")
		void success() {
			// given
			given(seasonRepository.findCurrentSeason(any())).willReturn(Optional.of(mock(Season.class)));
			// when, then
			seasonService.getCurSeason();
		}

		@Test
		@DisplayName("SeasonNotFoundException")
		void seasonNotFoundException() {
			// given
			given(seasonRepository.findCurrentSeason(any())).willReturn(Optional.empty());
			// when, then
			Assertions.assertThatThrownBy(() -> seasonService.getCurSeason())
				.isInstanceOf(SeasonNotFoundException.class);
		}
	}
}
