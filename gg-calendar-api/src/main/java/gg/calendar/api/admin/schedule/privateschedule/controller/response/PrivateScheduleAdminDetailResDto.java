package gg.calendar.api.admin.schedule.privateschedule.controller.response;

import java.time.LocalDateTime;

import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.TechTag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateScheduleAdminDetailResDto {

	private Long id;

	private DetailClassification detailClassification;

	private EventTag eventTag;

	private JobTag jobTag;

	private TechTag techTag;

	private String author;

	private String title;

	private String content;

	private String link;

	private String groupTitle;

	private String groupBackgroundColor;

	private boolean isAlarm;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@Builder
	private PrivateScheduleAdminDetailResDto(PrivateSchedule privateSchedule, ScheduleGroup scheduleGroup) {
		this.id = privateSchedule.getId();
		this.detailClassification = privateSchedule.getPublicSchedule().getClassification();
		this.eventTag = privateSchedule.getPublicSchedule().getEventTag();
		this.jobTag = privateSchedule.getPublicSchedule().getJobTag();
		this.techTag = privateSchedule.getPublicSchedule().getTechTag();
		this.author = privateSchedule.getPublicSchedule().getAuthor();
		this.title = privateSchedule.getPublicSchedule().getTitle();
		this.content = privateSchedule.getPublicSchedule().getContent();
		this.link = privateSchedule.getPublicSchedule().getLink();
		this.groupTitle = scheduleGroup.getTitle();
		this.groupBackgroundColor = scheduleGroup.getBackgroundColor();
		this.isAlarm = privateSchedule.getAlarm();
		this.startTime = privateSchedule.getPublicSchedule().getStartTime();
		this.endTime = privateSchedule.getPublicSchedule().getEndTime();

	}

	public static PrivateScheduleAdminDetailResDto toDto(PrivateSchedule privateSchedule, ScheduleGroup scheduleGroup) {
		return PrivateScheduleAdminDetailResDto.builder()
			.privateSchedule(privateSchedule)
			.scheduleGroup(scheduleGroup)
			.build();

	}
}
