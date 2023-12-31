package com.gg.server.admin.announcement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.exception.AnnounceDupException;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;
import com.gg.server.utils.annotation.UnitTest;
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
            given(announcementAdminRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(announcementList));
            announcementAdminService.findAllAnnouncement(mock(Pageable.class));
        }
    }

    @Nested
    @DisplayName("addAnnouncement_메서드_unitTest")
    class AddAnnouncementTest {
        @Test
        @DisplayName("성공")
        void success() {
            String IntraId = "intraId";
            LocalDateTime curTime = LocalDateTime.now();
            Announcement announcement = new Announcement();
            announcement.update(IntraId, curTime);
            given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
            announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto());
        }

        @Test
        @DisplayName("AnnounceNotFound")
        void announceNotFound() {
            given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.empty());
            assertThatThrownBy(()->announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto()))
                .isInstanceOf(AnnounceNotFoundException.class);
        }

        @Test
        @DisplayName("Announce_삭제_안된_경우")
        void AnnounceNotDeleted() {
            Announcement announcement = new Announcement();
            given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
            assertThatThrownBy(()->announcementAdminService.addAnnouncement(new AnnouncementAdminAddDto()))
                .isInstanceOf(AnnounceDupException.class);
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
        }

        @Test
        @DisplayName("AnnounceNotFound")
        void announceNotFound() {
            given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.empty());
            assertThatThrownBy(()->announcementAdminService.modifyAnnouncementIsDel(intraId))
                .isInstanceOf(AnnounceNotFoundException.class);
        }

        @Test
        @DisplayName("삭제된_Announce_삭제")
        void deleteFail() {
            Announcement announcement = new Announcement();
            announcement.update(intraId, LocalDateTime.now());
            given(announcementAdminRepository.findFirstByOrderByIdDesc()).willReturn(Optional.of(announcement));
            assertThatThrownBy(()->announcementAdminService.modifyAnnouncementIsDel(intraId))
                .isInstanceOf(AnnounceNotFoundException.class);
        }
    }
}