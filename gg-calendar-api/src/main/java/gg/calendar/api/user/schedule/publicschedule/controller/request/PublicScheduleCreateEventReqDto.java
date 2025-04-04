package gg.calendar.api.user.schedule.publicschedule.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class PublicScheduleCreateEventReqDto {

	@NotNull
	private EventTag eventTag;

	@NotBlank
	private String author;

	@NotBlank
	@Size(max = 50, message = "제목은 50자이하로 입력해주세요.")
	private String title;

	@Size(max = 2000, message = "내용은 2000자이하로 입력해주세요.")
	private String content;

	@Size(max = 255, message = "링크는 255자 이하로 입력해주세요.")
	private String link;

	@NotNull
	private LocalDateTime startTime;
	@NotNull
	private LocalDateTime endTime;

	@Builder
	public PublicScheduleCreateEventReqDto(EventTag eventTag, String author, String title, String content, String link,
		LocalDateTime startTime, LocalDateTime endTime) {
		this.eventTag = eventTag;
		this.author = author;
		this.title = title;
		this.content = content;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static PublicSchedule toEntity(String intraId, PublicScheduleCreateEventReqDto dto, ScheduleStatus status) {
		return PublicSchedule.builder()
			.classification(DetailClassification.EVENT)
			.eventTag(dto.eventTag)
			.author(intraId)
			.title(dto.title)
			.content(dto.content)
			.link(dto.link)
			.startTime(dto.startTime)
			.endTime(dto.endTime)
			.status(status)
			.build();
	}
}
