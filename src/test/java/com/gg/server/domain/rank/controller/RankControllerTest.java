package com.gg.server.domain.rank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.rank.dto.RankDto;
import com.gg.server.domain.rank.dto.RankPageResponseDto;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.service.RedisUploadService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class RankControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    SeasonRepository seasonRepository;

    @Autowired
    RankRedisRepository redisRepository;

    @Autowired
    RedisUploadService redisUploadService;

    @BeforeEach
    public void flushRedis(){
        redisRepository.deleteAll();
    }

    @AfterEach
    public void flushRedisAfter(){
        redisRepository.deleteAll();
        redisUploadService.uploadRedis();
    }

    @Test
    @DisplayName("/exp")
    void getExpRankPage() throws Exception {

        // given
        Season season = testDataUtils.createSeason();
        int myTotalExp = 1000;
        User myUser = testDataUtils.createNewUser(myTotalExp);
        testDataUtils.createUserRank(myUser, "1", season);

        int otherTotalExp = 2000;
        User user1 = testDataUtils.createNewUser(otherTotalExp);
        testDataUtils.createUserRank(user1, "2", season);
        int otherTotalExp2 = 3000;
        User user2 = testDataUtils.createNewUser(otherTotalExp2);
        testDataUtils.createUserRank(user2, "3", season);
        int otherTotalExp3 = 4000;
        User user3 = testDataUtils.createNewUser(otherTotalExp3);
        testDataUtils.createUserRank(user3, "4", season);

        int page = 1;
        int size = 3;
        String url = "/pingpong/exp?page=" + page + "&size=" + size;
        String accessToken = tokenProvider.createToken(myUser.getId());

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ExpRankPageResponseDto response = objectMapper.readValue(contentAsString, ExpRankPageResponseDto.class);

        //then
        Assertions.assertThat(response.getMyRank()).isEqualTo(4);
        Assertions.assertThat(response.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(response.getTotalPage()).isEqualTo(2);
        Assertions.assertThat(response.getRankList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("/pingpong/ranks/{gameType}")
    public void getRankPage () throws Exception
    {
        //given
        Season season = testDataUtils.createSeason();
        User myUser = testDataUtils.createNewUser();
        testDataUtils.createUserRank(myUser, "1", season, 1000);

        User user2 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user2, "2", season, 1500);

        User user3 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user3, "3", season, 2000);

        User user4 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user4, "4", season, 2500);

        User user5 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user5, "5", season, 3000);

        User user6 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user6, "6", season, 3500);

        User user7 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user7, "7", season, 4000);

        User user8 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user8, "8", season, 4500);

        User user9 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user9, "9", season, 5000);

        User user10 = testDataUtils.createNewUser();
        testDataUtils.createUserRank(user10, "10", season, 5500);

        String accessToken = tokenProvider.createToken(myUser.getId());

        int page = 4;
        int size = 3;
        String url = "/pingpong/ranks/single?page=" + page + "&size=" + size;

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        RankPageResponseDto resp = objectMapper.readValue(contentAsString, RankPageResponseDto.class);

        //then
        List<RankDto> rankList = resp.getRankList();
        Assertions.assertThat(resp.getCurrentPage()).isEqualTo(4);
        Assertions.assertThat(resp.getTotalPage()).isEqualTo(4);
        Assertions.assertThat(resp.getMyRank()).isEqualTo(10);
        Assertions.assertThat(rankList.size()).isEqualTo(1);
        Assertions.assertThat(rankList).isSortedAccordingTo(Comparator.comparing(RankDto::getPpp).reversed());
    }


}