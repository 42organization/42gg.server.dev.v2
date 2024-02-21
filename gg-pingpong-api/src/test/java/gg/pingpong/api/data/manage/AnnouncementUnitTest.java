package gg.pingpong.api.data.manage;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.admin.announcement.dto.AnnouncementAdminAddDto;
import gg.pingpong.data.manage.Announcement;
import gg.pingpong.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementUnitTest")
class AnnouncementUnitTest {
	final String content = "content";
	final String intraId = "intraId";

	@Nested
	@DisplayName("findLastAnnouncement_메서드_unitTest")
	class FindLastAnnounceContentTest {
		@Test
		@DisplayName("update_메서드_unitTest")
		void updateTest() {
			String deleterIntraId = "deleter";
			LocalDateTime curTime = LocalDateTime.now();
			Announcement announcement = new Announcement(content, intraId);
			announcement.update(deleterIntraId, curTime);
			assertThat(announcement.getDeleterIntraId()).isEqualTo(deleterIntraId);
			assertThat(announcement.getDeletedAt()).isEqualTo(curTime);
		}

		@Test
		@DisplayName("from_메서드_unitTest")
		void fromTest() {
			AnnouncementAdminAddDto dto = new AnnouncementAdminAddDto(content, intraId);
			Announcement announcement = Announcement.from(dto.getContent(), dto.getCreatorIntraId());
			assertThat(announcement.getContent()).isEqualTo(dto.getContent());
			assertThat(announcement.getCreatorIntraId()).isEqualTo(dto.getCreatorIntraId());
		}
	}
}
