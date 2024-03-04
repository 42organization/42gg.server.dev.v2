package gg.data.store;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;

import gg.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class CoinPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "attendance")
	private int attendance;

	@Column(name = "normal")
	private int normal;

	@Column(name = "rankWin")
	private int rankWin;

	@Column(name = "rankLose")
	private int rankLose;

	@CreatedDate
	@Column(name = "createdAt", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public CoinPolicy(User user, int attendance, int normal, int rankWin, int rankLose) {
		this.user = user;
		this.attendance = attendance;
		this.normal = normal;
		this.rankWin = rankWin;
		this.rankLose = rankLose;
		this.createdAt = LocalDateTime.now();
	}

	public static CoinPolicy from(User user, int attendance, int normal, int rankWin, int rankLose) {
		return CoinPolicy.builder()
			.user(user)
			.attendance(attendance)
			.normal(normal)
			.rankWin(rankWin)
			.rankLose(rankLose)
			.build();
	}
}
