package com.gg.server.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class UserControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;


//    @BeforeEach
//    public void addTestDataUtils() {
//        testDataUtils = new TestDataUtils(userRepository, tokenProvider, notiRepository,
//                seasonRepository, gameRepository);
//    }

    @Test
    @DisplayName("/pingpong/users/live")
    @Transactional
    @Rollback(value = false)
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

}