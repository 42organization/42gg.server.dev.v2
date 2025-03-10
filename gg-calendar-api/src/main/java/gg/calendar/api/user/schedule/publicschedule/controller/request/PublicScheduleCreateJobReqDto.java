package gg.calendar.api.user.schedule.publicschedule.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicScheduleCreateJobReqDto {

	@NotNull
	private JobTag jobTag;

	private TechTag techTag;

	@NotBlank
	private String author;

	@NotBlank
	@Size(max = 50, message = "제목은 50자이하로 입력해주세요.")
	private String title;

	@Size(max = 2000, message = "내용은 2000자이하로 입력해주세요.")
	private String content;

	private String link;

	@NotNull
	private LocalDateTime startTime;
	@NotNull
	private LocalDateTime endTime;

	@Builder
	public PublicScheduleCreateJobReqDto(JobTag jobTag, TechTag techTag, String author, String title, String content,
		String link,
		LocalDateTime startTime, LocalDateTime endTime) {
		this.jobTag = jobTag;
		this.techTag = techTag;
		this.author = author;
		this.title = title;
		this.content = content;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static PublicSchedule toEntity(String intraId, PublicScheduleCreateJobReqDto dto, ScheduleStatus status) {
		return PublicSchedule.builder()
			.classification(DetailClassification.JOB_NOTICE)
			.jobTag(dto.jobTag)
			.techTag(dto.techTag)
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
