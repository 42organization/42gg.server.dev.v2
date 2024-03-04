package gg.pingpong.api.user.feedback.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.data.manage.Feedback;
import gg.data.user.User;
import gg.pingpong.api.user.manage.controller.request.FeedbackRequestDto;
import gg.pingpong.api.user.manage.service.FeedbackService;
import gg.repo.manage.FeedbackRepository;
import gg.repo.user.UserRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.user.UserNotFoundException;

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
			Assertions.assertThatThrownBy(() -> feedbackService.addFeedback(new FeedbackRequestDto(), 1L))
				.isInstanceOf(UserNotFoundException.class);
			verify(userRepository, times(1)).findById(any(Long.class));
		}
	}
}
