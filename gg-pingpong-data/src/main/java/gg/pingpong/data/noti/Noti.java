package gg.pingpong.data.noti;

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
import javax.validation.constraints.NotNull;

import com.gg.server.data.noti.type.NotiType;
import com.gg.server.data.user.User;
import com.gg.server.global.utils.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Noti extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "noti_type", length = 30)
	@Enumerated(EnumType.STRING)
	private NotiType type;

	@Column(name = "message", length = 255)
	private String message;

	@NotNull
	@Column(name = "is_checked")
	private Boolean isChecked;

	public Noti(User user, NotiType type, String message, Boolean isChecked) {
		this.user = user;
		this.type = type;
		this.message = message;
		this.isChecked = isChecked;
	}

	public void update(User user, NotiType type, String message, Boolean isChecked) {
		this.user = user;
		this.type = type;
		this.message = message;
		this.isChecked = isChecked;
	}

	public void modifyIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}
}
