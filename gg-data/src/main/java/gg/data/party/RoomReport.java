package gg.data.party;

import javax.persistence.*;
import java.time.LocalDateTime;

import gg.data.user.User;

@Entity
public class RoomReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;

	@ManyToOne
	@JoinColumn(name = "reporter_id")
	private User reporter;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;

	@Column(length = 100)
	private String message;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	// Getters and setters...
}
