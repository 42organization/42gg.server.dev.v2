package gg.pingpong.api.user.feedback.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.user.manage.controller.FeedbackController;
import gg.pingpong.api.user.manage.controller.request.FeedbackRequestDto;
import gg.pingpong.api.user.manage.service.FeedbackService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackAdminServiceUnitTest")
class FeedbackControllerUnitTest {
	@Mock
	FeedbackService feedbackService;
	@InjectMocks
	FeedbackController feedbackController;

	@Nested
	@DisplayName("FeedbackSaveUnitTest")
	class FeedbackSaveUnitTest {
		@Test
		@DisplayName("success")
		void success() {
			// given
			UserDto userDto = UserDto.builder().id(1L).build();
			// when, then
			feedbackController.feedbackSave(mock(FeedbackRequestDto.class), userDto);
		}
	}
}
