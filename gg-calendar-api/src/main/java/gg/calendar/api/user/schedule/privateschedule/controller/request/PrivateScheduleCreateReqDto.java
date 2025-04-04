package gg.calendar.api.user.schedule.privateschedule.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateScheduleCreateReqDto {

	@NotBlank
	@Size(max = 50)
	private String title;

	@Size(max = 2000)
	private String content;

	@Size(max = 255, message = "링크는 255자 이하로 입력해주세요.")
	private String link;

	@NotNull
	private ScheduleStatus status;

	@NotNull
	private LocalDateTime startTime;

	@NotNull
	private LocalDateTime endTime;

	@NotNull
	private boolean alarm;

	@NotNull
	private Long groupId;

	@Builder
	private PrivateScheduleCreateReqDto(String title, String content, String link, LocalDateTime startTime,
		LocalDateTime endTime, boolean alarm, Long groupId, ScheduleStatus status) {
		this.title = title;
		this.content = content;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
		this.alarm = alarm;
		this.groupId = groupId;
		this.status = status;
	}

	public static PublicSchedule toEntity(String intraId, PrivateScheduleCreateReqDto privateScheduleCreateReqDto) {
		return PublicSchedule.builder()
			.classification(DetailClassification.PRIVATE_SCHEDULE)
			.author(intraId)
			.title(privateScheduleCreateReqDto.title)
			.content(privateScheduleCreateReqDto.content)
			.link(privateScheduleCreateReqDto.link)
			.startTime(privateScheduleCreateReqDto.startTime)
			.endTime(privateScheduleCreateReqDto.endTime)
			.status(privateScheduleCreateReqDto.status)
			.build();
	}
}
