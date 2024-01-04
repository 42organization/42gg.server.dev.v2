package com.gg.server.admin.feedback.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gg.server.admin.feedback.data.FeedbackAdminRepository;
import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.exception.FeedbackNotFoundException;
import com.gg.server.utils.annotation.UnitTest;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackAdminServiceUnitTest")
class FeedbackAdminServiceUnitTest {
    @Mock
    FeedbackAdminRepository feedbackAdminRepository;
    @InjectMocks
    FeedbackAdminService feedbackAdminService;

    @Nested
    @DisplayName("FindAllFeedbackUnitTest")
    class FindAllFeedbackUnitTest {
        @Test
        @DisplayName("success")
        public void success() {
            // given
            PageImpl<Feedback> feedbackList = new PageImpl<>(new ArrayList<>());
            given(feedbackAdminRepository.findAll(any(Pageable.class))).willReturn(feedbackList);
            feedbackAdminService.findAllFeedback(mock(Pageable.class));
        }
    }

    @Nested
    @DisplayName("ToggleFeedbackIsSolvedByAdmin")
    class ToggleFeedbackIsSolvedByAdmin {
        @Test
        @DisplayName("success")
        public void success() {
            // given
            Feedback feedback = new Feedback();
            ReflectionTestUtils.setField(feedback, "isSolved", true);
            given(feedbackAdminRepository.findById(any(Long.class))).willReturn(Optional.of(feedback));
            // when, then
            feedbackAdminService.toggleFeedbackIsSolvedByAdmin(1L);
            assertThat(feedback.getIsSolved()).isFalse();
        }

        @Test
        @DisplayName("feedback_not_found")
        public void feedbackNotFound() {
            // given
            given(feedbackAdminRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(()->feedbackAdminService.toggleFeedbackIsSolvedByAdmin(1L))
                .isInstanceOf(FeedbackNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("FindByPartsOfIntraId")
    class FindByPartsOfIntraId {
        @Test
        @DisplayName("success")
        public void success() {
            // given
            given(feedbackAdminRepository.findFeedbacksByUserIntraId(any(String.class))).willReturn(new ArrayList<>());
            // when, then
            feedbackAdminService.findByPartsOfIntraId("intraId", mock(Pageable.class));
        }
    }
}