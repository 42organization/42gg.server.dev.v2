package com.gg.server.domain.rank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("/vip")
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
        String url = "/pingpong/vip?page=" + page + "&size=-1";
        String accessToken = tokenProvider.createToken(myUser.getId());

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ExpRankPageResponseDto response = objectMapper.readValue(contentAsString, ExpRankPageResponseDto.class);

        //then
        Assertions.assertThat(response.getMyRank()).isEqualTo(4);
        Assertions.assertThat(response.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(response.getTotalPage()).isEqualTo(1);
        Assertions.assertThat(response.getRankList().size()).isEqualTo(4);
    }


}