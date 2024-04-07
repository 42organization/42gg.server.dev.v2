package gg.data.party;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import gg.data.BaseTimeEntity;
import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PartyPenalty extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "penalty_type", length = 20)
	private String penaltyType;

	@Column(name = "message", length = 100)
	private String message;

	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@NotNull
	@Column(name = "penalty_time")
	private int penaltyTime;

	public PartyPenalty(User user, String penaltyType, String message, LocalDateTime startTime, Integer penaltyTime) {
		this.user = user;
		this.penaltyType = penaltyType;
		this.message = message;
		this.startTime = startTime;
		this.penaltyTime = penaltyTime;
	}

	public static boolean isFreeFromPenalty(PartyPenalty partyPenalty) {
		return !LocalDateTime.now().isAfter(partyPenalty.getStartTime().plusMinutes(partyPenalty.getPenaltyTime()));
	}

	public void update(String penaltyType, String message, Integer penaltyTime) {
		this.penaltyType = penaltyType;
		this.message = message;
		this.penaltyTime = penaltyTime;
	}

}
