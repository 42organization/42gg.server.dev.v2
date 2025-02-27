package gg.data.calendar;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateSchedule extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_schedule_id", nullable = false)
	private PublicSchedule publicSchedule;

	@Column(nullable = false)
	private Boolean alarm;

	@Column(nullable = false)
	private Long groupId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private ScheduleStatus status;

	public PrivateSchedule(User user, PublicSchedule publicSchedule, Boolean alarm, Long groupId) {
		this.user = user;
		this.publicSchedule = publicSchedule;
		this.alarm = alarm;
		this.groupId = groupId;
		this.status = ScheduleStatus.ACTIVATE;
	}

	public void update(Boolean alarm, Long groupId) {
		this.alarm = alarm;
		this.groupId = groupId;
	}

	public void updateCascade(String title, String content, String link, LocalDateTime startTime, LocalDateTime endTime,
		boolean alarm, Long groupId) {
		this.alarm = alarm;
		this.groupId = groupId;
		this.publicSchedule.update(DetailClassification.PRIVATE_SCHEDULE, null, null, null, title, content, link,
			startTime, endTime);
	}

	public void delete() {
		this.status = ScheduleStatus.DELETE;
	}

	public void deleteCascade() {
		this.status = ScheduleStatus.DELETE;
		this.getPublicSchedule().delete();
	}
}
