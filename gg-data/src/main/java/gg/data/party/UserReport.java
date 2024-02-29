package gg.data.party;

import javax.persistence.*;
import java.time.LocalDateTime;

import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id")
	private User reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reportee_id")
	private User reportee;

	@Column(length = 100)
	private String message;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
