package gg.data.calendar;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import gg.data.BaseTimeEntity;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicSchedule extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
	private DetailClassification classification;

	@Enumerated(EnumType.STRING)
	@Column(length = 50, columnDefinition = "VARCHAR(50)")
	private EventTag eventTag;

	@Enumerated(EnumType.STRING)
	@Column(length = 50, columnDefinition = "VARCHAR(50)")
	private JobTag jobTag;

	@Enumerated(EnumType.STRING)
	@Column(length = 50, columnDefinition = "VARCHAR(50)")
	private TechTag techTag;

	@Column(nullable = false)
	private String author;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String content;

	private String link;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR(10)")
	private ScheduleStatus status;

	@Column(nullable = false)
	private Integer sharedCount;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@Builder
	private PublicSchedule(DetailClassification classification, EventTag eventTag, JobTag jobTag, TechTag techTag,
		String author, String title, String content, String link, ScheduleStatus status, LocalDateTime startTime,
		LocalDateTime endTime) {
		this.classification = classification;
		this.eventTag = eventTag;
		this.jobTag = jobTag;
		this.techTag = techTag;
		this.author = author;
		this.title = title;
		this.content = content;
		this.link = link;
		this.status = status;
		this.sharedCount = 0;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void update(DetailClassification classification, EventTag eventTag, JobTag jobTag, TechTag techTag,
		String title, String content, String link, LocalDateTime startTime, LocalDateTime endTime,
		ScheduleStatus status) {
		this.classification = classification;
		this.eventTag = eventTag;
		this.jobTag = jobTag;
		this.techTag = techTag;
		this.title = title;
		this.content = content;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
	}

	public void delete() {
		this.status = ScheduleStatus.DELETE;
	}

	// public void incrementSharedCount() {
	// 	this.sharedCount++;
	// }
}

