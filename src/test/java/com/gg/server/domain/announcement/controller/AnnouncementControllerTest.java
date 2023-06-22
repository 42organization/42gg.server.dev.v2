package com.gg.server.domain.announcement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.data.AnnouncementRepository;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;
import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.feedback.type.FeedbackType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
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

    @Test
    @Transactional
    @DisplayName("[GET]/pingpong/announcement")
    void getAnnouncement() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);


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
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        Announcement announcement = announcementRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new AnnounceNotFoundException());

        announcement.update("testId", LocalDateTime.now());

        String contentAsString = mockMvc.perform(get("/pingpong/announcement")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

}