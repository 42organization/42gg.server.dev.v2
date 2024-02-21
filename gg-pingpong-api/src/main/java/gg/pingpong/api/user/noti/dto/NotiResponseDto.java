package gg.pingpong.api.user.noti.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import gg.pingpong.data.noti.Noti;
import gg.pingpong.data.noti.type.NotiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotiResponseDto {
	private Long id;
	private NotiType type;
	private Boolean isChecked;
	private String message;
	private LocalDateTime createdAt;

	public static NotiResponseDto from(Noti noti) {
		NotiResponseDto notiResponseDto;
		if (noti == null) {
			notiResponseDto = null;
		} else {
			notiResponseDto = NotiResponseDto.builder()
				.id(noti.getId())
				.type(noti.getType())
				.isChecked(noti.getIsChecked())
				.message(noti.getMessage())
				.createdAt(noti.getCreatedAt())
				.build();
		}
		return notiResponseDto;
	}

	@Override
	public String toString() {
		return "NotiDto{"
			+ "id=" + id
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
		if (!(obj instanceof NotiResponseDto)) {
			return false;
		}
		NotiResponseDto other = (NotiResponseDto)obj;
		return Objects.equals(id, other.id)
			&& Objects.equals(type, other.type)
			&& Objects.equals(isChecked, other.isChecked)
			&& Objects.equals(message, other.message)
			&& Objects.equals(createdAt, other.createdAt);
	}
}
