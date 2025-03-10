package gg.calendar.api.user.schedule.publicschedule.controller.request;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gg.auth.UserDto;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import gg.utils.annotation.UnitTest;

@UnitTest
public class PublicScheduleCreateJobReqDtoTest {
	@Test
	@DisplayName("PublicScheduleCreateJobReqDto 생성 성공(techTag가 null인 경우)")
	void createJobPublicScheduleSuccess() {
		UserDto user = UserDto.builder().intraId("intraId").build();

		PublicScheduleCreateJobReqDto dto = PublicScheduleCreateJobReqDto.builder()
			.jobTag(JobTag.NEW_COMER)
			.author(user.getIntraId())
			.title("job create test")
			.content("job create test content!")
			.link("https://test.com")
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(2))
			.build();

		PublicSchedule schedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(), dto,
			ScheduleStatus.ACTIVATE);
		assertAll(() -> assertNotNull(schedule), () -> assertEquals(dto.getJobTag(), schedule.getJobTag()),
			() -> assertEquals(user.getIntraId(), schedule.getAuthor()),
			() -> assertEquals(dto.getTitle(), schedule.getTitle()),
			() -> assertEquals(dto.getContent(), schedule.getContent()),
			() -> assertEquals(dto.getLink(), schedule.getLink()),
			() -> assertEquals(dto.getStartTime(), schedule.getStartTime()),
			() -> assertEquals(dto.getEndTime(), schedule.getEndTime()));
	}

	@Test
	@DisplayName("PublicScheduleCreateJobReqDto 생성 성공(techTag가 null이 아닌 경우)")
	void createPublicScheduleSuccessWithTechtag() {
		UserDto user = UserDto.builder().intraId("intraId").build();

		PublicScheduleCreateJobReqDto dto = PublicScheduleCreateJobReqDto.builder()
			.jobTag(JobTag.NEW_COMER)
			.techTag(TechTag.BACK_END)
			.author(user.getIntraId())
			.title("job create test")
			.content("job create test content!")
			.link("https://test.com")
			.startTime(LocalDateTime.now().plusDays(1))
			.endTime(LocalDateTime.now().plusDays(2))
			.build();

		PublicSchedule schedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(), dto,
			ScheduleStatus.ACTIVATE);
		assertAll(() -> assertNotNull(schedule), () -> assertEquals(dto.getJobTag(), schedule.getJobTag()),
			() -> assertEquals(dto.getTechTag(), schedule.getTechTag()),
			() -> assertEquals(user.getIntraId(), schedule.getAuthor()),
			() -> assertEquals(dto.getTitle(), schedule.getTitle()),
			() -> assertEquals(dto.getContent(), schedule.getContent()),
			() -> assertEquals(dto.getLink(), schedule.getLink()),
			() -> assertEquals(dto.getStartTime(), schedule.getStartTime()),
			() -> assertEquals(dto.getEndTime(), schedule.getEndTime()));
	}
}
