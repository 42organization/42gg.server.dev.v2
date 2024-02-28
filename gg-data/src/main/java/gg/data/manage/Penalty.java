package gg.data.manage;

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
import javax.validation.constraints.NotNull;

import gg.data.BaseTimeEntity;
import gg.data.manage.type.PenaltyType;
import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Penalty extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "penalty_type", length = 20)
	@Enumerated(EnumType.STRING)
	private PenaltyType type;

	@Column(name = "message", length = 100)
	private String message;

	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@NotNull
	@Column(name = "penalty_time")
	private Integer penaltyTime;

	public Penalty(User user, PenaltyType type, String message, LocalDateTime startTime, Integer penaltyTime) {
		this.user = user;
		this.type = type;
		this.message = message;
		this.startTime = startTime;
		this.penaltyTime = penaltyTime;
	}

	public void updateStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}
