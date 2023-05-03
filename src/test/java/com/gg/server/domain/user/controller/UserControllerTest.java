package com.gg.server.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserNormalDetailResponseDto;
import com.gg.server.domain.user.dto.UserSearchResponseDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Test
    @DisplayName("/pingpong/users/live")
    public void userLiveTest() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/users/live";
        String event = "game";
        int notiCnt = 2;
        String currentMatchMode = "rank";
        testDataUtils.addMockDataUserLiveApi(event, notiCnt, currentMatchMode, userId);
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserLiveResponseDto userLiveResponseDto = objectMapper.readValue(contentAsString, UserLiveResponseDto.class);
        assertThat(userLiveResponseDto.getEvent()).isEqualTo(event);
        assertThat(userLiveResponseDto.getNotiCount()).isEqualTo(notiCnt);
        assertThat(userLiveResponseDto.getCurrentMatchMode()).isEqualTo(currentMatchMode);
    }
    
    @Test
    @DisplayName("/pingpong/users")
    public void userNormalDetail () throws Exception {
        //given
        String url = "/pingpong/users";
        String intraId = "intra";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserNormalDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserNormalDetailResponseDto.class);

        //then
        assertThat(responseDto.getIntraId()).isEqualTo(intraId);
        assertThat(responseDto.getUserImageUrl()).isEqualTo(imageUrl);
        assertThat(responseDto.getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("/pingpong/users/searches?q=${IntraId}")
    public void UserControllerTest() throws Exception
    {
        //given
        String intraId[] = {"intraId", "2intra2", "2intra", "aaaa", "bbbb"};
        String email = "email";
        String imageUrl = "imageUrl";
        for (String intra : intraId) {
            testDataUtils.createNewUser(intra, email, imageUrl, RacketType.PENHOLDER,
                    SnsType.BOTH, RoleType.ADMIN);
        }
        String accessToken = tokenProvider.createToken(1L);
        String keyWord = "intra";
        String url = "/pingpong/users/searches?q=" + keyWord;

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchResponseDto userSearchResponseDto = objectMapper.readValue(contentAsString, UserSearchResponseDto.class);

        //then
        assertThat(userSearchResponseDto.getUsers().size()).isEqualTo(3);
    }
}