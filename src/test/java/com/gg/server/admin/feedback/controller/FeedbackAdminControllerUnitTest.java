package com.gg.server.admin.feedback.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.gg.server.admin.feedback.dto.FeedbackAdminPageRequestDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackAdminControllerUnitTest")
class FeedbackAdminControllerUnitTest {
	@Mock
	FeedbackAdminService feedbackAdminService;
	@InjectMocks
	FeedbackAdminController feedbackAdminController;

	@Nested
	@DisplayName("FeedbackAllUnitTest")
	class FeedbackAllUnitTest {
		@Test
		@DisplayName("successWithIntraId")
		public void successWithIntraId() {
			//given
			FeedbackAdminPageRequestDto requestDto = new FeedbackAdminPageRequestDto("intraId", 2, 2);
			given(feedbackAdminService.findByPartsOfIntraId(any(String.class), any(Pageable.class)))
				.willReturn(mock(FeedbackListAdminResponseDto.class));
			// when, then
			feedbackAdminController.feedbackAll(requestDto);
		}

		@Test
		@DisplayName("successWithoutIntraId")
		public void successWithoutIntraId() {
			//given
			FeedbackAdminPageRequestDto requestDto = new FeedbackAdminPageRequestDto(null, 2, 2);
			given(feedbackAdminService.findAllFeedback(any(Pageable.class)))
				.willReturn(mock(FeedbackListAdminResponseDto.class));
			// when, then
			feedbackAdminController.feedbackAll(requestDto);
		}
	}

	@Nested
	@DisplayName("FeedbackIsSolvedToggleUnitTest")
	class FeedbackIsSolvedToggleUnitTest {
		@Test
		@DisplayName("success")
		public void success() {
			// given
			// when, then
			feedbackAdminController.feedbackIsSolvedToggle(1L);
		}
	}
}
