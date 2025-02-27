package gg.calendar.api.user.schedule.privateschedule.controller.response;

import java.time.LocalDateTime;

import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateScheduleDetailResDto {
	private Long id;

	private DetailClassification classification;

	private EventTag eventTag;

	private JobTag jobTag;

	private TechTag techTag;

	private String author;

	private String title;

	private String content;

	private String link;

	private ScheduleStatus status;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private boolean alarm;

	private String groupTitle;

	private String groupColor;

	@Builder
	private PrivateScheduleDetailResDto(Long id, DetailClassification classification, EventTag eventTag, JobTag jobTag,
		TechTag techTag, String author, String title, String content, String link, ScheduleStatus status,
		LocalDateTime startTime, LocalDateTime endTime, boolean alarm, String groupTitle, String groupColor) {
		this.id = id;
		this.classification = classification;
		this.eventTag = eventTag;
		this.jobTag = jobTag;
		this.techTag = techTag;
		this.author = author;
		this.title = title;
		this.content = content;
		this.link = link;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
		this.alarm = alarm;
		this.groupTitle = groupTitle;
		this.groupColor = groupColor;
	}

	public static PrivateScheduleDetailResDto toDto(PrivateSchedule privateSchedule, ScheduleGroup scheduleGroup) {
		return PrivateScheduleDetailResDto.builder()
			.id(privateSchedule.getId())
			.classification(privateSchedule.getPublicSchedule().getClassification())
			.eventTag(privateSchedule.getPublicSchedule().getEventTag())
			.jobTag(privateSchedule.getPublicSchedule().getJobTag())
			.techTag(privateSchedule.getPublicSchedule().getTechTag())
			.author(privateSchedule.getPublicSchedule().getAuthor())
			.title(privateSchedule.getPublicSchedule().getTitle())
			.content(privateSchedule.getPublicSchedule().getContent())
			.link(privateSchedule.getPublicSchedule().getLink())
			.status(privateSchedule.getStatus())
			.startTime(privateSchedule.getPublicSchedule().getStartTime())
			.endTime(privateSchedule.getPublicSchedule().getEndTime())
			.alarm(privateSchedule.getAlarm())
			.groupTitle(scheduleGroup.getTitle())
			.groupColor(scheduleGroup.getBackgroundColor())
			.build();
	}
}
