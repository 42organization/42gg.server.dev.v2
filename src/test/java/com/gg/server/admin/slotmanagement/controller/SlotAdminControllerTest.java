package com.gg.server.admin.slotmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
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
import java.util.List;

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

    @Test
    @DisplayName("[Get]/pingpong/admin/slot-management")
    void getSlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        String contentAsString = mockMvc.perform(get("/pingpong/admin/slot-management").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SlotListAdminResponseDto slotAdminDto = objectMapper.readValue(contentAsString, SlotListAdminResponseDto.class);

        for(SlotAdminDto dto : slotAdminDto.getSlotList()){
            System.out.println(dto.getFutureSlotTime());
            System.out.println(dto.getPastSlotTime());
            System.out.println(dto.getInterval());
            System.out.println(dto.getOpenMinute());
            System.out.println(dto.getStartTime());
            System.out.println(dto.getEndTime());
            System.out.println("----------------------");
        }


    }

    @Test
    @DisplayName("[Post]/pingpong/admin/slot-management")
    void modifySlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        SlotCreateRequestDto test = new SlotCreateRequestDto(4,1,20,1,LocalDateTime.now().plusHours(13));
        System.out.println(test.getStartTime());
        String content = objectMapper.writeValueAsString(test);

        String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        List<SlotManagement> slotList = adminSlotManagementRepository.findAllByOrderByCreatedAtDesc();
        for(SlotManagement slot : slotList){
            System.out.println(slot.getFutureSlotTime());
            System.out.println(slot.getPastSlotTime());
            System.out.println(slot.getGameInterval());
            System.out.println(slot.getOpenMinute());
            System.out.println(slot.getStartTime());
            System.out.println(slot.getEndTime());
            System.out.println("----------------------");
        }
    }

    @Test
    @DisplayName("[Delete]/pingpong/admin/slot-management")
    void delSlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        SlotCreateRequestDto test = new SlotCreateRequestDto(4,1,20,1,LocalDateTime.now().plusHours(13));
        System.out.println(test.getStartTime());
        String content = objectMapper.writeValueAsString(test);

        mockMvc.perform(post("/pingpong/admin/slot-management")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String contentAsString = mockMvc.perform(delete("/pingpong/admin/slot-management")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        List<SlotManagement> slotList = adminSlotManagementRepository.findAllByOrderByCreatedAtDesc();
        for(SlotManagement slot : slotList){
            System.out.println(slot.getFutureSlotTime());
            System.out.println(slot.getPastSlotTime());
            System.out.println(slot.getGameInterval());
            System.out.println(slot.getOpenMinute());
            System.out.println(slot.getStartTime());
            System.out.println(slot.getEndTime());
            System.out.println("----------------------");
        }
    }

}