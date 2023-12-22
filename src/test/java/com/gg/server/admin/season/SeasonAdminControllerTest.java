package com.gg.server.admin.season;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.admin.season.service.SeasonAdminService;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.google.common.net.HttpHeaders;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

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
        SeasonListAdminResponseDto seasonListAdminResponseDto = objectMapper.readValue(contentAsString, SeasonListAdminResponseDto.class);
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


        if (rankRedisRepository.findRankByUserId(redisHashKey, userId) == null)
            throw new SeasonNotFoundException();
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
            if (rankRedisRepository.findRankByUserId(redisHashKey, userId) != null)
                throw new SeasonForbiddenException();
        }
        catch(RedisDataNotFoundException ex){
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
    void failUpdateSeasons() throws Exception {//현재 시즌을 변경할 때 400번대 상태코드 반환
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