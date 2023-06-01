package com.gg.server.admin.slotmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SlotAdminControllerFailTest {
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

//이거 테스트 할려면 디비 내용 모두 지워야 함
//    @Test
//    @DisplayName("fail[Get]/pingpong/admin/slot-management")
//    void failGetSlotSetting() throws Exception {
//        String accessToken = testDataUtils.getLoginAccessToken();
//        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
//
//        String contentAsString = mockMvc.perform(get("/pingpong/admin/slot-management").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        System.out.println(contentAsString);
//    }

    @Test
    @DisplayName("fail[Post]/pingpong/admin/slot-management")
    void failModifySlotSetting() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        SlotCreateRequestDto test = new SlotCreateRequestDto(4,1,20,null,LocalDateTime.now().plusDays(2));
        String content = objectMapper.writeValueAsString(test);

        String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    @DisplayName("fail[Post]/pingpong/admin/slot-management")
    void 엔드타임_미래시점_보다_가까울_경우() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        SlotCreateRequestDto test = new SlotCreateRequestDto(4,1,20,1,LocalDateTime.now().plusHours(1));
        String content = objectMapper.writeValueAsString(test);

        String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }
}
