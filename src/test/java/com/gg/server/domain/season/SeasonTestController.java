package com.gg.server.domain.season;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.dto.SeasonListResDto;
import com.gg.server.domain.season.dto.SeasonResDto;
import com.gg.server.domain.season.service.SeasonService;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
public class SeasonTestController {

	@Autowired
	MockMvc mvc;
	@Autowired
	private SeasonService seasonService;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private SeasonRepository seasonRepository;

	@BeforeEach
	@Transactional
	public void init() {
		System.out.println("before each");
		Season s1 = new Season("test1 시즌", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100);
		seasonRepository.save(s1);
		seasonRepository.save(
			new Season("test2 season", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100));
		seasonRepository.flush();
		System.out.println(seasonRepository.findAll());
	}

	@Test
	@DisplayName("시즌 조회 Test")
	@Transactional
	void season_list_test() throws Exception {
		//given
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/seasons";
		List<SeasonResDto> list = seasonService.seasonList();
		//when
		String contentAsString = mvc.perform(
				RestDocumentationRequestBuilders.get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		//then
		SeasonListResDto result = objectMapper.readValue(contentAsString, SeasonListResDto.class);
		assertThat(list.size()).isEqualTo(result.getSeasonList().size());
	}
}
