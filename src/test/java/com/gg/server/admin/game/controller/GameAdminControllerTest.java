package com.gg.server.admin.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GameAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;



    @Test
    @DisplayName("[Get]/pingpong/admin/games/users?intraId=${intraId}&page=${pageNumber}&size={sizeNum}")
    void getUserGameList() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        Integer currentPage = 2;
        Integer pageSize = 5;//페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

        String url = "/pingpong/admin/games/users?intraId="
         + "nheo" + "&page=" + currentPage + "&size=" + pageSize;

        String contentAsString = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameLogListAdminResponseDto result = objectMapper.readValue(contentAsString, GameLogListAdminResponseDto.class);
        assertThat(result.getGameLogList().size()).isEqualTo(pageSize);
        System.out.println(result.getGameLogList().get(0).getGameId());
        System.out.println(result.getGameLogList().get(0).getStartAt());
        System.out.println(result.getGameLogList().get(0).getMode());

    }
}