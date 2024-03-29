package gg.data.party;

import static gg.utils.exception.BusinessChecker.*;
import static gg.utils.exception.ErrorCode.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import gg.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserRoom extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@Column(name = "nickname", length = 20)
	private String nickname;

	@Column(name = "is_exist")
	private boolean isExist;

	public UserRoom(User user, Room room, String randomNickname) {
		this.user = user;
		this.room = room;
		this.nickname = randomNickname;
		this.isExist = true;
	}

	public UserRoom(User user, Room room, String nickname, boolean isExist) {
		this.user = user;
		this.room = room;
		this.nickname = nickname;
		this.isExist = isExist;
	}

	public void updateIsExist(boolean isExist) {
		mustNotNull(isExist, NULL_POINT);
		this.isExist = isExist;
	}

	public boolean getIsExist() {
		return this.isExist;
	}
}
