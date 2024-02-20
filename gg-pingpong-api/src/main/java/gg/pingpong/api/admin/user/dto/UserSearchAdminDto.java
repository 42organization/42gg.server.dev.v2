package gg.pingpong.api.admin.user.dto;

import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSearchAdminDto {
	private Long id;
	private String intraId;
	private String statusMessage;
	private RoleType roleType;

	public UserSearchAdminDto(User user, String statusMessage) {
		this.id = user.getId();
		this.intraId = user.getIntraId();
		this.statusMessage = statusMessage;
		this.roleType = user.getRoleType();
	}

	@Override
	public String toString() {
		return "{"
			+ "id=" + id
			+ ", intraId='" + intraId + '\''
			+ ", statusMessage='" + statusMessage + '\''
			+ ", roleType=" + roleType
			+ '}';
	}
}
