package gg.pingpong.api.user.announcement.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.user.manage.service.AnnouncementService;
import gg.pingpong.data.manage.Announcement;
import gg.pingpong.repo.announcement.AnnouncementRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.announcement.AnnounceNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementServiceUnitTest")
class AnnouncementServiceUnitTest {
	@Mock
	AnnouncementRepository announcementRepository;
	@InjectMocks
	AnnouncementService announcementService;

	@Nested
	@DisplayName("findLastAnnouncement_메서드_unitTest")
	class FindLastAnnounceContentTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			String content = "content";
			String intraId = "intraId";
			Announcement announcement = new Announcement(content, intraId);
			given(announcementRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			// when, then
			assertThat(announcementService.findLastAnnouncement().getContent()).isEqualTo(content);
			verify(announcementRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("성공_삭제된_announcement")
		void successAnnouncementDeleted() {
			//given
			String content = "content";
			String intraId = "intraId";
			Announcement announcement = new Announcement(content, intraId);
			announcement.update(intraId, LocalDateTime.now());
			given(announcementRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			// when, then
			assertThat(announcementService.findLastAnnouncement().getContent()).isEqualTo("");
			verify(announcementRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("Announcement_404")
		void announcementNotFound() {
			//given
			given(announcementRepository.findFirstByOrderByIdDesc()).willReturn(Optional.empty());
			// when, then
			assertThatThrownBy(() -> announcementService.findLastAnnouncement())
				.isInstanceOf(AnnounceNotFoundException.class);
			verify(announcementRepository, times(1)).findFirstByOrderByIdDesc();
		}
	}
}
