package gg.calendar.api.user.schedule.publicschedule.controller.request;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gg.auth.UserDto;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.utils.annotation.UnitTest;

@UnitTest
public class PublicScheduleCreateEventReqDtoTest {
	@Test
	@DisplayName("PublicScheduleCreateEventReqDto 생성 성공")
	void createEventPublicScheduleSuccess() {
		UserDto user = UserDto.builder().intraId("intraId").build();

		PublicScheduleCreateEventReqDto dto = PublicScheduleCreateEventReqDto.builder()
			.eventTag(EventTag.INSTRUCTION)
			.author(user.getIntraId())
			.title("event create test")
			.content("event create test content!")
			.link("https://test.com")
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(2))
			.build();

		PublicSchedule schedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(), dto,
			ScheduleStatus.ACTIVATE);
		assertAll(() -> assertNotNull(schedule), () -> assertEquals(dto.getEventTag(), schedule.getEventTag()),
			() -> assertEquals(user.getIntraId(), schedule.getAuthor()),
			() -> assertEquals(dto.getTitle(), schedule.getTitle()),
			() -> assertEquals(dto.getContent(), schedule.getContent()),
			() -> assertEquals(dto.getLink(), schedule.getLink()),
			() -> assertEquals(dto.getStartTime(), schedule.getStartTime()),
			() -> assertEquals(dto.getEndTime(), schedule.getEndTime()));
	}
}
