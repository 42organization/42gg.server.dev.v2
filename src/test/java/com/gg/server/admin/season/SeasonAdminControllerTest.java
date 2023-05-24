package com.gg.server.admin.season;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.admin.season.data.SeasonAdminRepository;

import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.admin.season.service.SeasonAdminService;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
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

    @BeforeEach
    void setUp() {
        Season test1 = Season.builder()
                .seasonName("test1")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .startPpp(1000)
                .pppGap(500)
                .build();
        Season test2 = Season.builder()
                .seasonName("test2")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .startPpp(1000)
                .pppGap(500)
                .build();

        seasonAdminRepository.save(test1);
        seasonAdminRepository.save(test2);

        List<Season> seasons =  seasonAdminRepository.findAll();
        List<SeasonAdminDto> dtoList = new ArrayList<>();
        for (Season season : seasons) {
            SeasonAdminDto dto = new SeasonAdminDto(season);
            dtoList.add(dto);
        }
        responseDto = new SeasonListAdminResponseDto(dtoList);
    }

    @Test
    @DisplayName("[GET]pingpong/admin/seasons")
    void getAdminSeasons() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);


        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get("/pingpong/admin/seasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
        SeasonListAdminResponseDto seasonListAdminResponseDto = objectMapper.readValue(contentAsString, SeasonListAdminResponseDto.class);
        System.out.println(seasonListAdminResponseDto.getSeasonList().size());
        System.out.println(seasonListAdminResponseDto.getSeasonList().get(1).getSeasonName() + " : " + responseDto.getSeasonList().get(1).getSeasonName());
        assertThat(seasonListAdminResponseDto.getSeasonList().size()).isEqualTo(responseDto.getSeasonList().size());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getSeasonName()).isEqualTo(responseDto.getSeasonList().get(0).getSeasonName());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getStartTime()).isEqualTo(responseDto.getSeasonList().get(0).getStartTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getEndTime()).isEqualTo(responseDto.getSeasonList().get(0).getEndTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getStartPpp()).isEqualTo(responseDto.getSeasonList().get(0).getStartPpp());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getPppGap()).isEqualTo(responseDto.getSeasonList().get(0).getPppGap());
    }

    @Test
    @DisplayName("[POST]/pingpong/admin/seasons")
    void createSeasons() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        SeasonCreateRequestDto seasonCreateReqeustDto = SeasonCreateRequestDto.builder()
                .seasonName("redis1")
                .startTime(LocalDateTime.now().plusDays(1))
                .startPpp(1000)
                .pppGap(500)
                .build();
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
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

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
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

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
        SeasonUpdateRequestDto seasonUpdateRequestDto = SeasonUpdateRequestDto.builder()
                .seasonName("putSeasonTestName")
                .startTime(LocalDateTime.now().plusDays(1))
                .startPpp(1000)
                .pppGap(500)
                .build();
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
}