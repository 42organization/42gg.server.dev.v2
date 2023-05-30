package com.gg.server.admin.slotmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SlotAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    adminSlotManagementRepository adminSlotManagementRepository;

    @BeforeEach
    void setUp() {
        SlotManagement test = SlotManagement.builder()
                .pastSlotTime(1)
                .futureSlotTime(12)
                .openMinute(5)
                .gameInterval(15)
                .build();

        adminSlotManagementRepository.save(test);
    }

    @Test
    @DisplayName("[Get]/pingpong/admin/slot-management")
    void getSlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        String contentAsString = mockMvc.perform(get("/pingpong/admin/slot-management").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SlotListAdminResponseDto slotAdminDto = objectMapper.readValue(contentAsString, SlotListAdminResponseDto.class);
        assertThat(slotAdminDto.getSlotList().get(0).getPastSlotTime()).isEqualTo(1);
        assertThat(slotAdminDto.getSlotList().get(0).getFutureSlotTime()).isEqualTo(12);
        assertThat(slotAdminDto.getSlotList().get(0).getOpenMinute()).isEqualTo(5);
        assertThat(slotAdminDto.getSlotList().get(0).getInterval()).isEqualTo(15);


    }

    @Test
    @DisplayName("[Post]/pingpong/admin/slot-management")
    void modifySlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        SlotManagement test = SlotManagement.builder()
                .pastSlotTime(4)
                .futureSlotTime(1)
                .openMinute(1)
                .gameInterval(20)
                .startTime(LocalDateTime.now().plusDays(2))
                .build();
        String content = objectMapper.writeValueAsString(test);

        String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

}