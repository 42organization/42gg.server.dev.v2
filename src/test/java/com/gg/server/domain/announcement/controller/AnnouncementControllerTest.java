package com.gg.server.domain.announcement.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.data.AnnouncementRepository;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
class AnnouncementControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    AnnouncementRepository announcementRepository;

    @BeforeEach
    void setUp() {
        User admin = testDataUtils.createAdminUser();
        testDataUtils.createAnnouncements(admin, 5);
    }

    @Test
    @Transactional
    @DisplayName("[GET]/pingpong/announcement")
    void getAnnouncement() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();

        String contentAsString = mockMvc.perform(get("/pingpong/announcement")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }


    @Test
    @Transactional
    @DisplayName("[GET]/pingpong/announcement")
    void getAnnouncementEmpty() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Announcement announcement = announcementRepository.findFirstByOrderByIdDesc()
            .orElseThrow(AnnounceNotFoundException::new);

        announcement.update("testId", LocalDateTime.now());

        String contentAsString = mockMvc.perform(get("/pingpong/announcement")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

}