package gg.pingpong.api.admin.announcement.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

import gg.admin.repo.manage.AnnouncementAdminRepository;
import gg.data.manage.Announcement;
import gg.pingpong.api.admin.manage.dto.AnnouncementAdminAddDto;
import gg.pingpong.api.admin.manage.service.AnnouncementAdminService;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.announcement.AnnounceDupException;
import gg.utils.exception.announcement.AnnounceNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementAdminServiceUnitTest")
class AnnouncementAdminServiceUnitTest {
	@Mock
	AnnouncementAdminRepository announcementAdminRepository;
	@InjectMocks
	AnnouncementAdminService announcementAdminService;

	@Nested
	@DisplayName("findAllAnnouncement_메서드_unitTest")
	class FindAllAnnouncementTest {
		@Test
		@DisplayName("성공")
		void success() {
			List<Announcement> announcementList = new ArrayList<>();
			given(announcementAdminRepository.findAll(any(Pageable.class)))
				.willReturn(new PageImpl<>(announcementList));
			announcementAdminService.findAllAnnouncement(mock(Pageable.class));
			verify(announcementAdminRepository, times(1)).findAll(any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("addAnnouncement_메서드_unitTest")
	class AddAnnouncementTest {
		@Test
		@DisplayName("성공")
		void success() {
			String intraId = "intraId";
			LocalDateTime curTime = LocalDateTime.now();
			Announcement announcement = new Announcement();
			announcement.update(intraId, curTime);
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto());
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("AnnounceNotFound")
		void announceNotFound() {
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.empty());
			assertThatThrownBy(() -> announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto()))
				.isInstanceOf(AnnounceNotFoundException.class);
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("Announce_삭제_안된_경우")
		void announceNotDeleted() {
			Announcement announcement = new Announcement();
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			assertThatThrownBy(() -> announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto()))
				.isInstanceOf(AnnounceDupException.class);
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}
	}

	@Nested
	@DisplayName("modifyAnnouncementIsDel_메서드_unitTest")
	class ModifyAnnouncementIsDelTest {
		final String intraId = "intraId";

		@Test
		@DisplayName("성공")
		void success() {
			Announcement announcement = new Announcement();
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			announcementAdminService.modifyAnnouncementIsDel(intraId);
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("AnnounceNotFound")
		void announceNotFound() {
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.empty());
			assertThatThrownBy(() -> announcementAdminService.modifyAnnouncementIsDel(intraId))
				.isInstanceOf(AnnounceNotFoundException.class);
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}

		@Test
		@DisplayName("삭제된_Announce_삭제")
		void deleteFail() {
			Announcement announcement = new Announcement();
			announcement.update(intraId, LocalDateTime.now());
			given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
			assertThatThrownBy(() -> announcementAdminService.modifyAnnouncementIsDel(intraId))
				.isInstanceOf(AnnounceNotFoundException.class);
			verify(announcementAdminRepository, times(1)).findFirstByOrderByIdDesc();
		}
	}
}
