package gg.data.party;

import javax.persistence.*;

import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@Column(name = "nickname", length = 40)
	private String nickname;

	@Column(name = "is_exist")
	private Boolean isExist;
}
