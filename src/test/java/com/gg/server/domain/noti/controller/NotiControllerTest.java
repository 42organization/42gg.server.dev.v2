package com.gg.server.domain.noti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
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

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class NotiControllerTest {

    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private NotiService notiService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("pingpong/notifications")
    @Transactional
    public void NotiFindByUserTest() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/notifications";

        UserDto userDto = UserDto.from(userRepository.getById(userId));
        NotiResponseDto expectedResponse = new NotiResponseDto(notiService.findNotiByUser(userDto));
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        NotiResponseDto actureResponse= objectMapper.readValue(contentAsString, NotiResponseDto.class);

        //then
        assertThat(actureResponse, equalTo(expectedResponse));
    }
}