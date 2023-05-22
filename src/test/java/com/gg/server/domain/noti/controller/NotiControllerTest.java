package com.gg.server.domain.noti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiListResponseDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    NotiRepository notiRepository;
    @Autowired
    private NotiService notiService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /pingpong/notifications")
    @Transactional
    public void NotiFindByUserTest() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/notifications";

        UserDto userDto = UserDto.from(userRepository.getById(userId));
        NotiListResponseDto expectedResponse = new NotiListResponseDto(notiService.findNotiByUser(userDto));
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        NotiListResponseDto actureResponse= objectMapper.readValue(contentAsString, NotiListResponseDto.class);

        //then
        assertThat(actureResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("PUT /pingpong/notifications/check")
    @Transactional
    public void checkNotiByUserTest() throws Exception{
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/notifications/check";
        User user = userRepository.findById(userId).get();

        notiRepository.save(new Noti(user, NotiType.ANNOUNCE, "announce", false));
        notiRepository.save(new Noti(user, NotiType.MATCHED, "matched", false));
        notiRepository.save(new Noti(user, NotiType.IMMINENT, "imminent", true));
        notiRepository.save(new Noti(user, NotiType.CANCELEDBYMAN, "canceledbyman", false));
        notiRepository.save(new Noti(user, NotiType.CANCELEDBYTIME, "canceledbytime", false));
        //when
        String contentAsString = mockMvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        //then
        List<Noti> notiList = notiRepository.findByUser(user);
        for (Noti noti : notiList) {
            assertThat(noti.getIsChecked()).isTrue();
        }
    }

    @Test
    @DisplayName("DELETE /notifications")
    @Transactional
    public void notiRemoveAll() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/notifications";
        User user = userRepository.findById(userId).get();

        notiRepository.save(new Noti(user, NotiType.ANNOUNCE, "announce", false));
        notiRepository.save(new Noti(user, NotiType.MATCHED, "matched", false));
        notiRepository.save(new Noti(user, NotiType.IMMINENT, "imminent", true));
        notiRepository.save(new Noti(user, NotiType.CANCELEDBYMAN, "canceledbyman", false));
        notiRepository.save(new Noti(user, NotiType.CANCELEDBYTIME, "canceledbytime", false));

        //when
        String contentAsString = mockMvc.perform(delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        //then
        List<Noti> notiList = notiRepository.findByUser(user);
        assertThat(notiList.size()).isEqualTo(0);
    }
}