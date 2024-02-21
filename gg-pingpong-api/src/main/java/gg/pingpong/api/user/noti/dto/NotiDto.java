package gg.pingpong.api.user.noti.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.noti.Noti;
import gg.pingpong.data.noti.type.NotiType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotiDto {
	private Long id;
	private UserDto user;
	private NotiType type;
	private Boolean isChecked;
	private String message;
	private LocalDateTime createdAt;

	public static NotiDto from(Noti noti) {
		NotiDto notiDto;
		if (noti == null) {
			notiDto = null;
		} else {
			notiDto = NotiDto.builder()
				.id(noti.getId())
				.user(UserDto.from(noti.getUser()))
				.type(noti.getType())
				.isChecked(noti.getIsChecked())
				.message(noti.getMessage())
				.createdAt(noti.getCreatedAt())
				.build();
		}
		return notiDto;
	}

	@Override
	public String toString() {
		return "NotiDto{"
			+ "id=" + id
			+ ", user=" + user
			+ ", type=" + type
			+ ", isChecked=" + isChecked
			+ ", message='" + message + '\''
			+ ", createdDate=" + createdAt
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof NotiDto)) {
			return false;
		}
		NotiDto other = (NotiDto)obj;
		return Objects.equals(id, other.id)
			&& Objects.equals(user, other.user)
			&& Objects.equals(type, other.type)
			&& Objects.equals(isChecked, other.isChecked)
			&& Objects.equals(message, other.message)
			&& Objects.equals(createdAt, other.createdAt);
	}
}
