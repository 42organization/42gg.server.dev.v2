package gg.pingpong.api.admin.season.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;

import gg.admin.repo.season.SeasonAdminRepository;
import gg.data.rank.Tier;
import gg.data.season.Season;
import gg.pingpong.api.admin.rank.service.RankAdminService;
import gg.pingpong.api.admin.rank.service.RankRedisAdminService;
import gg.pingpong.api.admin.season.controller.request.SeasonCreateRequestDto;
import gg.pingpong.api.admin.season.controller.request.SeasonUpdateRequestDto;
import gg.pingpong.api.admin.season.controller.response.SeasonListAdminResponseDto;
import gg.pingpong.api.admin.season.dto.SeasonAdminDto;
import gg.pingpong.api.admin.season.service.SeasonAdminService;
import gg.auth.utils.AuthTokenProvider;
import gg.repo.rank.RankRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.utils.RedisKeyManager;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.exception.rank.RedisDataNotFoundException;
import gg.utils.exception.season.SeasonForbiddenException;
import gg.utils.exception.season.SeasonNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class SeasonAdminControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private SeasonAdminRepository seasonAdminRepository;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	RankRepository rankRepository;

	@Autowired
	RankRedisRepository rankRedisRepository;

	@Autowired
	SeasonAdminService seasonAdminService;

	@Autowired
	RankAdminService rankAdminService;

	@Autowired
	RankRedisAdminService rankRedisAdminService;

	SeasonListAdminResponseDto responseDto;
	Long dbSeasonId;

	@AfterEach
	void tearDown() {
		rankRedisRepository.deleteAll();
	}

	@Test
	@DisplayName("[GET]pingpong/admin/seasons")
	void getAdminSeasons() throws Exception {
		testDataUtils.createTierSystem("pingpong");
		testDataUtils.createSeason();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get("/pingpong/admin/seasons")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
		SeasonListAdminResponseDto seasonListAdminResponseDto = objectMapper.readValue(contentAsString,
			SeasonListAdminResponseDto.class);
		System.out.println(seasonListAdminResponseDto.getSeasonList().size());
	}

	@Test
	@DisplayName("[POST]/pingpong/admin/seasons")
	void createSeasons() throws Exception {
		List<Tier> pingpong = testDataUtils.createTierSystem("pingpong");
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		SeasonCreateRequestDto seasonCreateReqeustDto = new SeasonCreateRequestDto(
			"redis1",
			LocalDateTime.now().plusHours(25),
			1000,
			500);
		String content = objectMapper.writeValueAsString(seasonCreateReqeustDto);

		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/pingpong/admin/seasons")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isCreated())
			.andReturn().getResponse().getContentAsString();

		dbSeasonId = rankRepository.findFirstByOrderByCreatedAtDesc().get().getSeason().getId();
		String redisHashKey = RedisKeyManager.getHashKey(dbSeasonId);

		if (rankRedisRepository.findRankByUserId(redisHashKey, userId) == null) {
			throw new SeasonNotFoundException();
		}
		System.out.println(rankRedisRepository.findRankByUserId(redisHashKey, userId).getIntraId());
	}

	@Test
	@DisplayName("[Delete]/pingpong/admin/season/{seasonId}")
	void deleteSeasons() throws Exception {
		testDataUtils.createTierSystem("pingpong");
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Season newSeason = Season.builder()
			.seasonName("redis1")
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(10))
			.startPpp(1000)
			.pppGap(500)
			.build();
		seasonAdminRepository.save(newSeason);
		Long seasonId = newSeason.getId();

		SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
		if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
			rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
			rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
		}
		dbSeasonId = seasonId;

		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.delete("/pingpong/admin/seasons/" + dbSeasonId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();

		try {
			String redisHashKey = RedisKeyManager.getHashKey(dbSeasonId);
			if (rankRedisRepository.findRankByUserId(redisHashKey, userId) != null) {
				throw new SeasonForbiddenException();
			}
		} catch (RedisDataNotFoundException ex) {
			System.out.println("success: 레디스가 지워져 있습니다");
		}
	}

	@Test
	@DisplayName("[Put]/pingpong/admin/seasons/{seasonId}")
	void updateSeasons() throws Exception {
		testDataUtils.createTierSystem("pingpong");
		testDataUtils.createSeason();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Season newSeason = Season.builder()
			.seasonName("redis1")
			.startTime(LocalDateTime.now().plusHours(25))
			.endTime(LocalDateTime.now().plusDays(10))
			.startPpp(1000)
			.pppGap(500)
			.build();
		seasonAdminRepository.save(newSeason);
		Long seasonId = newSeason.getId();

		SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
		if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
			rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
			rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
		}
		dbSeasonId = seasonId;
		SeasonUpdateRequestDto seasonUpdateRequestDto = new SeasonUpdateRequestDto(
			"putSeasonTestName",
			LocalDateTime.now().plusHours(25),
			1000,
			500);

		String content = objectMapper.writeValueAsString(seasonUpdateRequestDto);

		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put("/pingpong/admin/seasons/" + dbSeasonId)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();

		assertThat(seasonAdminRepository.findById(dbSeasonId).get().getSeasonName())
			.isEqualTo(seasonUpdateRequestDto.getSeasonName());
	}

	@Test
	@DisplayName("Fail[Put]/pingpong/admin/seasons/{seasonId}")
	void failUpdateSeasons() throws Exception { //현재 시즌을 변경할 때 400번대 상태코드 반환
		testDataUtils.createTierSystem("pingpong");
		testDataUtils.createSeason();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Long nowSeasonId = seasonAdminRepository.findCurrentSeason(LocalDateTime.now().plusMinutes(1)).get().getId();
		SeasonUpdateRequestDto seasonUpdateRequestDto = new SeasonUpdateRequestDto(
			"putSeasonTestName",
			LocalDateTime.now().plusHours(25),
			1000,
			500);

		String content = objectMapper.writeValueAsString(seasonUpdateRequestDto);

		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put("/pingpong/admin/seasons/" + nowSeasonId)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is4xxClientError())//403반환
			.andReturn().getResponse().getContentAsString();

	}
}
