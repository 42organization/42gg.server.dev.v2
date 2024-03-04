package gg.data.party;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;

import gg.data.BaseTimeEntity;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_id")
	private User host;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id")
	private User creator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "title", length = 15)
	private String title;

	@Column(name = "content", length = 100)
	private String content;

	@Column(name = "max_people")
	private Integer maxPeople;

	@Column(name = "min_people")
	private Integer minPeople;

	@Column(name = "due_date")
	private LocalDateTime dueDate;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private RoomType status;

	@Builder
	public Room(User host, User creator, Category category, String title, String content, Integer maxPeople,
		Integer minPeople, LocalDateTime dueDate, RoomType status) {
		this.host = host;
		this.creator = creator;
		this.category = category;
		this.title = title;
		this.content = content;
		this.maxPeople = maxPeople;
		this.minPeople = minPeople;
		this.dueDate = dueDate;
		this.status = status;
	}
}
