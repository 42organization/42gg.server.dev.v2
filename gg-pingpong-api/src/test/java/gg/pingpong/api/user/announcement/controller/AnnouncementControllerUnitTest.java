package gg.pingpong.api.user.announcement.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.user.announcement.dto.AnnouncementDto;
import gg.pingpong.api.user.announcement.controller.response.AnnouncementResponseDto;
import gg.pingpong.api.user.announcement.service.AnnouncementService;
import gg.pingpong.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementControllerUnitTest")
class AnnouncementControllerUnitTest {
	@Mock
	AnnouncementService announcementService;
	@InjectMocks
	AnnouncementController announcementController;

	@Nested
	@DisplayName("findLastAnnounceContent_메서드_unitTest")
	class FindLastAnnounceContentTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			String content = "content";
			given(announcementService.findLastAnnouncement()).willReturn(new AnnouncementDto(content));
			// when, then
			assertThat(announcementController.findLastAnnounceContent()).isInstanceOf(AnnouncementResponseDto.class);
			assertThat(announcementController.findLastAnnounceContent().getContent()).isEqualTo(content);
		}
	}
}
