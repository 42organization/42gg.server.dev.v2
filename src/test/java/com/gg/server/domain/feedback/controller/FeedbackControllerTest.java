package com.gg.server.domain.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.feedback.type.FeedbackType;
import com.gg.server.domain.season.data.Season;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class FeedbackControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Test
    @Transactional
    @DisplayName("[Post]/pingpong/feedback")
    void getAnnouncementList() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        System.out.println(userId);

        FeedbackRequestDto addDto = FeedbackRequestDto.builder()
                .content("하나하나둘둘테스트")
                .category(FeedbackType.ETC)
                .build();

        String content = objectMapper.writeValueAsString(addDto);
        String url = "/pingpong/feedback";

        String contentAsString = mockMvc.perform(post(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Feedback result = feedbackRepository.findFirstByOrderByIdDesc();
        assertThat(result.getCategory()).isEqualTo(addDto.getCategory());
        assertThat(result.getContent()).isEqualTo(addDto.getContent());

        System.out.println(result.getId() + ", " + result.getUser().getIntraId());
    }

}