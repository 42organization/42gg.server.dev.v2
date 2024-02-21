package gg.pingpong.api.domain.feedback.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.feedback.service.FeedbackService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.utils.annotation.UnitTest;

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
