package com.gg.server.admin.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.feedback.data.FeedbackAdminRepository;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.type.FeedbackType;
import com.gg.server.domain.user.UserRepository;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FeedbackAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    FeedbackAdminRepository feedbackAdminRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("[Get]/pingpong/admin/feedback")
    void getFeedback() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        Integer currentPage = 1;
        Integer pageSize = 5;//페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

        String url = "/pingpong/admin/feedback?page=" + currentPage + "&size=" + pageSize;

        String contentAsString = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        FeedbackListAdminResponseDto result = objectMapper.readValue(contentAsString, FeedbackListAdminResponseDto.class);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getFeedbackList().size()).isEqualTo(5);
        System.out.println(result.getFeedbackList().get(0).getId());
        System.out.println(result.getFeedbackList().get(0).getContent());

    }

    @Test
    @DisplayName("[Patch]pingpong/admin/feedback/{id}")
    void patchFeedback() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        //Id 44는 개발자가 만든 본섭?? 테스트 피드백이다.
        Feedback feedback = Feedback.builder()
                .category(FeedbackType.ETC)
                .content("test1234")
                .user(userRepository.findById(userId).get())
                .build();
        feedbackAdminRepository.save(feedback);

        String url = "/pingpong/admin/feedback/" + feedback.getId().toString();
        Boolean status = feedback.getIsSolved();

        String contentAsString = mockMvc.perform(patch(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();


    }
}