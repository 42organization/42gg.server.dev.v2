package gg.data.party;

import javax.persistence.*;

import gg.data.user.User;

@Entity
public class UserRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userroomId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;

	@Column(length = 40)
	private String nickname;

	@Column(name = "is_exist")
	private Boolean isExist;

	// Getters and setters...
}
