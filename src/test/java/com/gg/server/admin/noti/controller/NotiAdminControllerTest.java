package com.gg.server.admin.noti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.NotiAdminDto;
import com.gg.server.admin.noti.dto.NotiListAdminResponseDto;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiListResponseDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
    NotiAdminRepository notiAdminRepository;
    @Autowired
    NotiRepository notiRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /pingpong/admin/notifications")
    @Transactional
    public void getAllNotiTest() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.findById(userId).get();
        String testMessage = "Test notification";
        notiAdminService.sendAnnounceNotiToUser(new SendNotiAdminRequestDto(user.getIntraId(), testMessage));
        String url = "/pingpong/admin/notifications?page=1&size=20";
        String url2 = "/pingpong/admin/notifications?page=1&q=\"" + user.getIntraId() + "\"";

        //when
        //200 성공(전체조회)
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        NotiListAdminResponseDto actureResponse1 = objectMapper.readValue(contentAsString, NotiListAdminResponseDto.class);
        //200 성공(검색조회)
        String contentAsString2 = mockMvc.perform(get(url2).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        NotiListAdminResponseDto actureResponse2 = objectMapper.readValue(contentAsString, NotiListAdminResponseDto.class);
        Pageable pageable = PageRequest.of(0, 20);
                Sort.by("createdAt").descending().and(Sort.by("user.intraId").ascending());

        //then
        List<Noti> notiList1 = notiAdminRepository.findAll(pageable).getContent();
        List<NotiAdminDto> expectedNotiAdminDtoList = actureResponse1.getNotifications();
        for (int i = 0; i < notiList1.size(); i++) {
            Assertions.assertThat(expectedNotiAdminDtoList.get(i).getIntraId()).isEqualTo(expectedNotiAdminDtoList.get(i).getIntraId());
            Assertions.assertThat(expectedNotiAdminDtoList.get(i).getMessage()).isEqualTo(expectedNotiAdminDtoList.get(i).getMessage());
            Assertions.assertThat(expectedNotiAdminDtoList.get(i).getIsChecked()).isEqualTo(expectedNotiAdminDtoList.get(i).getIsChecked());
            Assertions.assertThat(expectedNotiAdminDtoList.get(i).getType()).isEqualTo(expectedNotiAdminDtoList.get(i).getType());
        }

        List<Noti> notiList2 = notiRepository.findByUser(user);
        Assertions.assertThat(notiList2.size()).isEqualTo(1);
        Noti expectedNoti2 = notiList2.get(0);
        NotiAdminDto actureNotiResponseDto2 = actureResponse2.getNotifications().get(0);
        Assertions.assertThat(expectedNoti2.getUser().getIntraId()).isEqualTo(actureNotiResponseDto2.getIntraId());
        Assertions.assertThat(expectedNoti2.getMessage()).isEqualTo(testMessage);
        Assertions.assertThat(expectedNoti2.getIsChecked()).isFalse();
        Assertions.assertThat(expectedNoti2.getType()).isEqualTo(NotiType.ANNOUNCE);
    }

    @Test
    @DisplayName("POST /pingpong/admin/notifications")
    @Transactional
    public void sendNotiToUserTest() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.findById(userId).get();
        String url = "/pingpong/admin/notifications";
        String wrongIntraId = UUID.randomUUID().toString().substring(0, 30);
        String expectedMessage = "test 알림 message ";

        //when
            //201 성공
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SendNotiAdminRequestDto(user.getIntraId(), expectedMessage))))
                .andExpect(status().isCreated());

            //400 존재하지 않는 intraId
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SendNotiAdminRequestDto(wrongIntraId, expectedMessage))))
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