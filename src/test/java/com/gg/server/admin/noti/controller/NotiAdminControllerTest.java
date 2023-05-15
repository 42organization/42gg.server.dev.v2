package com.gg.server.admin.noti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class NotiAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotiAdminService notiAdminService;
    @Autowired
    NotiRepository notiRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("/pingpong/admin/notifications/{intraId}")
    @Transactional
    public void sendNotiToUser() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        User user = userRepository.findById(userId).get();
        String url = "/pingpong/admin/notifications/" + user.getIntraId();
        String wrongUrl = "/pingpong/admin/notifications/" + UUID.randomUUID().toString().substring(0, 30);
        String expectedMessage = "test 알림 message ";

        //when
            //201 성공
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SendNotiAdminRequestDto(expectedMessage))))
                .andExpect(status().isCreated());

            //400 존재하지 않는 intraId
        mockMvc.perform(post(wrongUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SendNotiAdminRequestDto(expectedMessage))))
                .andExpect(status().isBadRequest());

        //then
        List<Noti> notiList = notiRepository.findByUser(user);
        Assertions.assertThat(notiList.size()).isEqualTo(1);
        Noti actureNoti = notiList.get(0);
        Assertions.assertThat(actureNoti.getUser()).isEqualTo(user);
        Assertions.assertThat(actureNoti.getMessage()).isEqualTo(expectedMessage);
        Assertions.assertThat(actureNoti.getIsChecked()).isFalse();
        Assertions.assertThat(actureNoti.getType()).isEqualTo(NotiType.ANNOUNCE);
    }
}