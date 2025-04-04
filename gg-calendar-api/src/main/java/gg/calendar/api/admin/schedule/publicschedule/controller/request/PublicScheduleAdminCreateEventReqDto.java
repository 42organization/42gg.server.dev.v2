package gg.calendar.api.admin.schedule.publicschedule.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.ScheduleStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicScheduleAdminCreateEventReqDto {

	@NotNull
	private EventTag eventTag;

	@NotBlank
	@Size(max = 50, message = "제목은 50자이하로 입력해주세요.")
	private String title;

	@Size(max = 2000, message = "내용은 2000자이하로 입력해주세요.")
	private String content;

	@Size(max = 255, message = "링크는 255자 이하로 입력해주세요.")
	private String link;

	@NotNull
	private ScheduleStatus status;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;

	@Builder
	public PublicScheduleAdminCreateEventReqDto(EventTag eventTag, String title, String content, String link,
		ScheduleStatus status, LocalDateTime startTime,
		LocalDateTime endTime) {
		this.eventTag = eventTag;
		this.title = title;
		this.content = content;
		this.link = link;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static PublicSchedule toEntity(PublicScheduleAdminCreateEventReqDto publicScheduleAdminCreateEventReqDto) {

		return PublicSchedule.builder()
			.classification(DetailClassification.EVENT)
			.eventTag(publicScheduleAdminCreateEventReqDto.eventTag)
			.author("42GG")
			.title(publicScheduleAdminCreateEventReqDto.title)
			.content(publicScheduleAdminCreateEventReqDto.content)
			.link(publicScheduleAdminCreateEventReqDto.link)
			.status(publicScheduleAdminCreateEventReqDto.status)
			.startTime(publicScheduleAdminCreateEventReqDto.startTime)
			.endTime(publicScheduleAdminCreateEventReqDto.endTime)
			.build();
	}
}
