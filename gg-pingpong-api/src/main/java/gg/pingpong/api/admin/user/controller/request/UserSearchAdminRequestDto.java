package gg.pingpong.api.admin.user.controller.request;

import gg.pingpong.api.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchAdminRequestDto extends PageRequestDto {
	private String intraId;
	private String userFilter;

	public UserSearchAdminRequestDto(Integer page, Integer size, String intraId, String userFilter) {
		super(page, size);
		this.intraId = intraId;
		this.userFilter = userFilter;
	}
}
