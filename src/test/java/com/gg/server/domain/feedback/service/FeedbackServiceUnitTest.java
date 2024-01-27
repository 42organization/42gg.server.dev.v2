package com.gg.server.domain.feedback.service;

import static org.mockito.BDDMockito.*;

import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.utils.annotation.UnitTest;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackServiceUnitTest")
class FeedbackServiceUnitTest {
    @Mock
    UserRepository userRepository;
    @Mock
    FeedbackRepository feedbackRepository;
    @InjectMocks
    FeedbackService feedbackService;

    @Nested
    @DisplayName("FeedbackSaveUnitTest")
    class FeedbackSaveUnitTest {
        @Test
        @DisplayName("success")
        void success() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(mock(User.class)));
            given(feedbackRepository.save(any(Feedback.class))).willReturn(mock(Feedback.class));
            // when, then
            feedbackService.addFeedback(new FeedbackRequestDto(), 1L);
            verify(userRepository, times(1)).findById(any(Long.class));
            verify(feedbackRepository, times(1)).save(any(Feedback.class));
        }

        @Test
        @DisplayName("user_not_found")
        void userNotFound() {
            // given
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            Assertions.assertThatThrownBy(()->feedbackService.addFeedback(new FeedbackRequestDto(), 1L))
                .isInstanceOf(UserNotFoundException.class);
            verify(userRepository, times(1)).findById(any(Long.class));
        }
    }
}