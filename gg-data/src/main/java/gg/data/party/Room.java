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

	@Column
	private Integer maxPeople;

	@Column
	private Integer minPeople;

	@Column
	private LocalDateTime dueDate;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private RoomType roomStatus;
}
