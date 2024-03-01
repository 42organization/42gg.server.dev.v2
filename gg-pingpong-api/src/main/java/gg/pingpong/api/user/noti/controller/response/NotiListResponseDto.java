package gg.pingpong.api.user.noti.controller.response;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiListResponseDto {
	private List<NotiResponseDto> notifications;

	@Override
	public String toString() {
		return "NotiResponseDto{"
			+ "notifications=" + notifications
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		NotiListResponseDto other = (NotiListResponseDto)obj;
		return Objects.equals(notifications, other.notifications);
	}
}
