package gg.pingpong.api.admin.noti.controller.request;

import gg.pingpong.api.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotiListAdminRequestDto extends PageRequestDto {
	private String intraId;

	public NotiListAdminRequestDto(Integer page, Integer size, String intraId) {
		super(page, size);
		this.intraId = intraId;
	}
}
