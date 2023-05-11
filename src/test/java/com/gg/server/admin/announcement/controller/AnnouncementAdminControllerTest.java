package com.gg.server.admin.announcement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.slot.dto.SlotAdminDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnnouncementAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    AnnouncementAdminRepository announcementAdminRepository;

    @Test
    @DisplayName("[Get]/pingpong/admin/announcement")
    void getAnnouncementList() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        Integer currentPage = 3;
        Integer pageSize = 10;

        String url = "/pingpong/admin/announcement?page?=" + currentPage + "&size?=" + pageSize;

        String contentAsString = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        AnnouncementAdminListResponseDto announceListDto = objectMapper.readValue(contentAsString, AnnouncementAdminListResponseDto.class);

        System.out.println("11111111111");
        System.out.println(announceListDto.getAnnouncementList());
        assertThat(announceListDto.getCurrentPage()).isEqualTo(currentPage);
        assertThat(announceListDto.getAnnouncementList().size()).isEqualTo(pageSize);
    }

}