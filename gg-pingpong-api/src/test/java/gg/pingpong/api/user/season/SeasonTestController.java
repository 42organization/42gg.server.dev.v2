package gg.pingpong.api.user.season;

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

import gg.auth.utils.AuthTokenProvider;
import gg.data.pingpong.season.Season;
import gg.pingpong.api.user.season.controller.response.SeasonListResDto;
import gg.pingpong.api.user.season.controller.response.SeasonResDto;
import gg.pingpong.api.user.season.service.SeasonService;
import gg.repo.season.SeasonRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
public class SeasonTestController {

	@Autowired
	MockMvc mvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private SeasonService seasonService;
	@Autowired
	private SeasonRepository seasonRepository;

	@BeforeEach
	public void init() {
		System.out.println("before each");
		Season s1 = new Season("test1 시즌", LocalDateTime.now().withNano(0).minusMinutes(5),
			LocalDateTime.now().plusMinutes(15), 1000,
			100);
		seasonRepository.save(s1);
		seasonRepository.save(
			new Season("test2 season", LocalDateTime.now().withNano(0).minusMinutes(5),
				LocalDateTime.now().plusMinutes(15), 1000,
				100));
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
