package gg.data.party;
import gg.data.party.type.RoomType;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@ManyToOne
	@JoinColumn(name = "host_id")
	private User host;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "title", length = 15)
	private String title;

	@Column(name = "content", length = 100)
	private String content;

	@Column
	private Integer maxPeople;

	@Column
	private Integer minPeople;

	@Column
	private LocalDateTime dueDate;

	@Column
	private LocalDateTime createDate;

	@Enumerated(EnumType.STRING)
	private RoomType roomStatus;

	// Getters and setters?
}
