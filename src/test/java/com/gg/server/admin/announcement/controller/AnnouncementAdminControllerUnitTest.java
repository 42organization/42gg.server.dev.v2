package com.gg.server.admin.announcement.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.service.AnnouncementAdminService;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementAdminControllerUnitTest")
class AnnouncementAdminControllerUnitTest {
	@Mock
	AnnouncementAdminService announcementAdminService;
	@InjectMocks
	AnnouncementAdminController announcementAdminController;

	@Nested
	@DisplayName("GetAnnouncementList_메서드_unitTest")
	class GetAnnouncementList {
		@Test
		@DisplayName("성공")
		void success() {
			given(announcementAdminService.findAllAnnouncement(any(Pageable.class))).willReturn(
				new AnnouncementAdminListResponseDto());
			announcementAdminController.getAnnouncementList(new PageRequestDto(1, 5));
		}
	}

	@Nested
	@DisplayName("AddAnnouncement_메서드_unitTest")
	class AddAnnouncement {
		@Test
		@DisplayName("성공")
		void success() {
			announcementAdminController.addAnnouncement(mock(AnnouncementAdminAddDto.class));
		}
	}

	@Nested
	@DisplayName("AnnouncementModify_메서드_unitTest")
	class AnnouncementModify {
		@Test
		@DisplayName("성공")
		void success() {
			announcementAdminController.announcementModify("deleterIntraId");
		}
	}
}
